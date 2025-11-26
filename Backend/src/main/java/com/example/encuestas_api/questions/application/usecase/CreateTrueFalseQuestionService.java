package com.example.encuestas_api.questions.application.usecase;

import com.example.encuestas_api.questions.application.port.in.CreateTrueFalseQuestionUseCase;
import com.example.encuestas_api.questions.application.port.out.CheckFormExistsPort;
import com.example.encuestas_api.questions.application.port.out.CheckSectionBelongsToFormPort;
import com.example.encuestas_api.questions.application.port.out.ComputeNextQuestionPositionPort;
import com.example.encuestas_api.questions.application.port.out.SaveQuestionPort;
import com.example.encuestas_api.questions.domain.model.Option;
import com.example.encuestas_api.questions.domain.model.Question;
import com.example.encuestas_api.questions.domain.valueobject.Prompt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

@Service
@Transactional
public class CreateTrueFalseQuestionService implements CreateTrueFalseQuestionUseCase {

    private final CheckFormExistsPort formExists;
    private final CheckSectionBelongsToFormPort sectionBelongs;
    private final ComputeNextQuestionPositionPort nextPos;
    private final SaveQuestionPort savePort;
    private final Clock clock;

    public CreateTrueFalseQuestionService(CheckFormExistsPort formExists,
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
                           boolean shuffleOptions, boolean trueIsCorrect, String trueLabel, String falseLabel) {

        if (formId == null || !formExists.exists(formId)) throw new IllegalArgumentException("formId inválido");
        if (!sectionBelongs.belongs(formId, sectionId)) throw new IllegalArgumentException("sectionId no pertenece al form");
        int position = nextPos.nextPosition(formId, sectionId);

        String t = (trueLabel == null || trueLabel.isBlank()) ? "Verdadero" : trueLabel.trim();
        String f = (falseLabel == null || falseLabel.isBlank()) ? "Falso" : falseLabel.trim();

        var optTrue  = Option.newOf(t, trueIsCorrect);
        var optFalse = Option.newOf(f, !trueIsCorrect);

        var now = Instant.now(clock);
        var q = Question.newTrueFalse(formId, sectionId, position, Prompt.of(prompt), helpText, required, shuffleOptions, optTrue, optFalse, now);
        return savePort.save(q);
    }
}
