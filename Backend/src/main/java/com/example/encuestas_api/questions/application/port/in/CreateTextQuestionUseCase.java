package com.example.encuestas_api.questions.application.port.in;

import com.example.encuestas_api.questions.domain.model.Question;

public interface CreateTextQuestionUseCase {
    Question handle(Long formId,
                    Long sectionId,
                    String prompt,
                    String helpText,
                    boolean required,
                    String textMode,
                    String placeholder,
                    Integer minLength,
                    Integer maxLength
    );
}
