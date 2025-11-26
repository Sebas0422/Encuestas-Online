package com.example.encuestas_api.questions.application.port.in;

import com.example.encuestas_api.questions.domain.model.Question;

public interface CreateTrueFalseQuestionUseCase {
    Question handle(Long formId,
                    Long sectionId,
                    String prompt,
                    String helpText,
                    boolean required,
                    boolean shuffleOptions,
                    boolean trueIsCorrect,
                    String trueLabel,
                    String falseLabel
    );
}
