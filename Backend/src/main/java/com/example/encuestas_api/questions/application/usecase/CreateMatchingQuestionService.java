package com.example.encuestas_api.questions.application.usecase;

import com.example.encuestas_api.questions.application.port.in.CreateMatchingQuestionUseCase;
import com.example.encuestas_api.questions.application.port.out.CheckFormExistsPort;
import com.example.encuestas_api.questions.application.port.out.CheckSectionBelongsToFormPort;
import com.example.encuestas_api.questions.application.port.out.ComputeNextQuestionPositionPort;
import com.example.encuestas_api.questions.application.port.out.CreateMatchingQuestionPort;
import com.example.encuestas_api.questions.domain.model.Question;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CreateMatchingQuestionService implements CreateMatchingQuestionUseCase {

    private final CheckFormExistsPort formExists;
    private final CheckSectionBelongsToFormPort sectionBelongs;
    private final ComputeNextQuestionPositionPort nextPos;
    private final CreateMatchingQuestionPort createPort;

    public CreateMatchingQuestionService(CheckFormExistsPort formExists,
                                         CheckSectionBelongsToFormPort sectionBelongs,
                                         ComputeNextQuestionPositionPort nextPos,
                                         CreateMatchingQuestionPort createPort) {
        this.formExists = formExists;
        this.sectionBelongs = sectionBelongs;
        this.nextPos = nextPos;
        this.createPort = createPort;
    }

    @Override
    public Question handle(Long formId, Long sectionId, String prompt, String helpText, boolean required,
                           boolean shuffleRightColumn, List<String> leftTexts, List<String> rightTexts, List<PairIdx> keyPairs) {

        if (formId == null || !formExists.exists(formId)) throw new IllegalArgumentException("formId inválido");
        if (!sectionBelongs.belongs(formId, sectionId)) throw new IllegalArgumentException("sectionId no pertenece al form");

        int position = nextPos.nextPosition(formId, sectionId);
        return createPort.create(formId, sectionId, position, prompt, helpText, required,
                shuffleRightColumn, leftTexts, rightTexts, keyPairs);
    }
}
