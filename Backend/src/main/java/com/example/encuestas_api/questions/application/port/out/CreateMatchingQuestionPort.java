package com.example.encuestas_api.questions.application.port.out;

import com.example.encuestas_api.questions.application.port.in.CreateMatchingQuestionUseCase.PairIdx;
import com.example.encuestas_api.questions.domain.model.Question;

import java.util.List;

public interface CreateMatchingQuestionPort {
    Question create(Long formId,
                    Long sectionId,
                    int position,
                    String prompt,
                    String helpText,
                    boolean required,
                    boolean shuffleRightColumn,
                    List<String> leftTexts,
                    List<String> rightTexts,
                    List<PairIdx> keyPairs);
}
