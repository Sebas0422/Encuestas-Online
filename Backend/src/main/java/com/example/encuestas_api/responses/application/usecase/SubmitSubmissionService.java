package com.example.encuestas_api.responses.application.usecase;

import com.example.encuestas_api.responses.application.dto.SubmitSubmissionCommand;
import com.example.encuestas_api.responses.application.exception.ResponsePolicyViolationException;
import com.example.encuestas_api.responses.application.exception.SubmissionValidationException;
import com.example.encuestas_api.responses.application.port.in.SubmitSubmissionUseCase;
import com.example.encuestas_api.responses.application.port.out.*;
import com.example.encuestas_api.responses.domain.model.Submission;
import com.example.encuestas_api.responses.domain.service.SubmissionValidator;
import com.example.encuestas_api.responses.domain.valueobject.QuestionSnapshot;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;

@Service
@Transactional
public class SubmitSubmissionService implements SubmitSubmissionUseCase {

    private final FindSubmissionPort findPort;
    private final SaveSubmissionPort savePort;
    private final BuildQuestionSnapshotsPort snapshotsPort;
    private final LoadFormPoliciesPort policiesPort;
    private final CountSubmittedByFormPort countPort;

    public SubmitSubmissionService(FindSubmissionPort findPort,
                                   SaveSubmissionPort savePort,
                                   BuildQuestionSnapshotsPort snapshotsPort,
                                   LoadFormPoliciesPort policiesPort,
                                   CountSubmittedByFormPort countPort) {
        this.findPort = findPort;
        this.savePort = savePort;
        this.snapshotsPort = snapshotsPort;
        this.policiesPort = policiesPort;
        this.countPort = countPort;
    }

    @Override
    public Submission handle(SubmitSubmissionCommand cmd) {
        Submission s = findPort.findById(cmd.submissionId())
                .orElseThrow(() -> new IllegalArgumentException("Submission no encontrada"));

        var p = policiesPort.getPolicies(s.getFormId());
        Instant now = Instant.now();
        if (p.openAt() != null && now.isBefore(p.openAt())) throw new ResponsePolicyViolationException("El formulario aún no está abierto");
        if (p.closeAt() != null && now.isAfter(p.closeAt())) throw new ResponsePolicyViolationException("El formulario ya está cerrado");

        Map<Long, QuestionSnapshot> snapshots = snapshotsPort.byFormId(s.getFormId());
        var errors = SubmissionValidator.validate(s, snapshots);
        if (!errors.isEmpty()) {
            errors.forEach(e -> {
                System.out.println("[SUBMIT INVALID] qid=" + e.getQuestionId() + " reason=" + e.getMessage());
            });
            throw new SubmissionValidationException(errors);
        }

        if (!errors.isEmpty()) throw new SubmissionValidationException(errors);

        if (p.limitMode() == LoadFormPoliciesPort.ResponseLimitMode.LIMITED_N) {
            long count = countPort.countSubmittedByForm(s.getFormId());
            if (p.limitedN() != null && count >= p.limitedN())
                throw new ResponsePolicyViolationException("Se alcanzó el límite de respuestas permitidas");
        }

        s.markSubmitted();
        return savePort.save(s);
    }
}
