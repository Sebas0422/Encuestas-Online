package com.example.encuestas_api.responses.application.usecase;

import com.example.encuestas_api.responses.application.dto.StartSubmissionCommand;
import com.example.encuestas_api.responses.application.exception.ResponsePolicyViolationException;
import com.example.encuestas_api.responses.application.port.in.StartSubmissionUseCase;
import com.example.encuestas_api.responses.application.port.out.*;
import com.example.encuestas_api.responses.domain.model.Submission;
import com.example.encuestas_api.responses.domain.valueobject.Respondent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Objects;

@Service
@Transactional

public class StartSubmissionService implements StartSubmissionUseCase {

    private final SaveSubmissionPort savePort;
    private final LoadFormPoliciesPort policiesPort;
    private final ExistsSubmittedByRespondentPort existsPort;
    private final CountSubmittedByFormPort countPort;

    public StartSubmissionService(SaveSubmissionPort savePort,
                                  LoadFormPoliciesPort policiesPort,
                                  ExistsSubmittedByRespondentPort existsPort,
                                  CountSubmittedByFormPort countPort) {
        this.savePort = savePort;
        this.policiesPort = policiesPort;
        this.existsPort = existsPort;
        this.countPort = countPort;
    }

    @Override
    public Submission handle(StartSubmissionCommand cmd) {
        Objects.requireNonNull(cmd, "cmd");
        var p = policiesPort.getPolicies(cmd.formId());

        Instant now = Instant.now();
        if (p.openAt() != null && now.isBefore(p.openAt())) throw new ResponsePolicyViolationException("El formulario aún no está abierto");
        if (p.closeAt() != null && now.isAfter(p.closeAt())) throw new ResponsePolicyViolationException("El formulario ya está cerrado");

        Respondent respondent = switch (cmd.respondentType()) {
            case ANONYMOUS -> {
                if (!p.anonymousAllowed()) throw new ResponsePolicyViolationException("Este formulario no permite respuestas anónimas");
                yield Respondent.anonymous();
            }
            case USER -> Respondent.user(cmd.userId());
            case EMAIL -> Respondent.email(cmd.email());
            case CODE  -> Respondent.code(cmd.code());
        };

        switch (p.limitMode()) {
            case ONE_PER_RESPONDENT -> {
                if (respondent.getUserId() != null && existsPort.existsSubmittedByFormAndUser(cmd.formId(), respondent.getUserId()))
                    throw new ResponsePolicyViolationException("Ya existe una respuesta enviada por este usuario");
                if (respondent.getEmail() != null && existsPort.existsSubmittedByFormAndEmail(cmd.formId(), respondent.getEmail()))
                    throw new ResponsePolicyViolationException("Ya existe una respuesta enviada para este email");
                if (respondent.getCode() != null && existsPort.existsSubmittedByFormAndCode(cmd.formId(), respondent.getCode()))
                    throw new ResponsePolicyViolationException("Ya existe una respuesta enviada para este código");
            }
            case LIMITED_N -> {
                long count = countPort.countSubmittedByForm(cmd.formId());
                if (p.limitedN() != null && count >= p.limitedN())
                    throw new ResponsePolicyViolationException("Se alcanzó el límite de respuestas permitidas");
            }
            case UNLIMITED -> {}
        }

        Submission s = new Submission(cmd.formId(), respondent);
        s.setSourceIp(cmd.sourceIp());
        return savePort.save(s);
    }
}
