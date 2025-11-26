package com.example.encuestas_api.questions.application.usecase;

import com.example.encuestas_api.questions.application.port.in.CreateTextQuestionUseCase;
import com.example.encuestas_api.questions.application.port.out.CheckFormExistsPort;
import com.example.encuestas_api.questions.application.port.out.CheckSectionBelongsToFormPort;
import com.example.encuestas_api.questions.application.port.out.ComputeNextQuestionPositionPort;
import com.example.encuestas_api.questions.application.port.out.SaveQuestionPort;
import com.example.encuestas_api.questions.domain.model.Question;
import com.example.encuestas_api.questions.domain.model.TextMode;
import com.example.encuestas_api.questions.domain.model.TextSettings;
import com.example.encuestas_api.questions.domain.valueobject.Prompt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

@Service
@Transactional
public class CreateTextQuestionService implements CreateTextQuestionUseCase {

    private final CheckFormExistsPort formExists;
    private final CheckSectionBelongsToFormPort sectionBelongs;
    private final ComputeNextQuestionPositionPort nextPos;
    private final SaveQuestionPort savePort;
    private final Clock clock;

    public CreateTextQuestionService(CheckFormExistsPort formExists,
                                     CheckSectionBelongsToFormPort sectionBelongs,
                                     ComputeNextQuestionPositionPort nextPos,
                                     SaveQuestionPort savePort,
                                     Clock clock) {
        this.formExists = formExists;
        this.sectionBelongs = sectionBelongs;
        this.nextPos = nextPos;
        this.savePort = savePort;
        this.clock = clock;
    }

    @Override
    public Question handle(Long formId, Long sectionId, String prompt, String helpText, boolean required,
                           String textMode, String placeholder, Integer minLength, Integer maxLength) {

        if (formId == null || !formExists.exists(formId)) throw new IllegalArgumentException("formId inválido");
        if (!sectionBelongs.belongs(formId, sectionId)) throw new IllegalArgumentException("sectionId no pertenece al form");
        int position = nextPos.nextPosition(formId, sectionId);

        TextMode mode = (textMode == null ? TextMode.SHORT : TextMode.valueOf(textMode));
        TextSettings settings = TextSettings.of(mode, placeholder, minLength, maxLength);

        var now = Instant.now(clock);
        var q = Question.newText(formId, sectionId, position, Prompt.of(prompt), helpText, required, settings, now);
        return savePort.save(q);
    }
}
