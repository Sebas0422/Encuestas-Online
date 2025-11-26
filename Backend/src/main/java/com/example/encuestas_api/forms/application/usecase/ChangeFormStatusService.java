package com.example.encuestas_api.forms.application.usecase;

import com.example.encuestas_api.forms.application.port.in.ChangeFormStatusUseCase;
import com.example.encuestas_api.forms.application.port.out.CountQuestionsByFormPort;
import com.example.encuestas_api.forms.application.port.out.LoadFormPort;
import com.example.encuestas_api.forms.application.port.out.SaveFormPort;
import com.example.encuestas_api.forms.domain.exception.FormNotFoundException;
import com.example.encuestas_api.forms.domain.model.Form;
import com.example.encuestas_api.forms.domain.model.FormStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

@Service
@Transactional
public class ChangeFormStatusService implements ChangeFormStatusUseCase {

    private final LoadFormPort loadPort;
    private final SaveFormPort savePort;
    private final CountQuestionsByFormPort countQuestions;
    private final Clock clock;

    public ChangeFormStatusService(LoadFormPort loadPort,
                                   SaveFormPort savePort,
                                   CountQuestionsByFormPort countQuestions,
                                   Clock clock) {
        this.loadPort = loadPort;
        this.savePort = savePort;
        this.countQuestions = countQuestions;
        this.clock = clock;
    }

    @Override
    public Form handle(Long formId, FormStatus target) {
        var form = loadPort.loadById(formId).orElseThrow(() -> new FormNotFoundException(formId));

        if (target == FormStatus.published && countQuestions != null) {
            long n = countQuestions.countByFormId(formId);
            if (n <= 0) throw new IllegalStateException("No se puede publicar: el formulario no tiene preguntas");
        }

        var updated = form.changeStatus(target, Instant.now(clock));
        return savePort.save(updated);
    }
}
