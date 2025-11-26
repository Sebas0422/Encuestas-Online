package com.example.encuestas_api.questions.application.port.in;

import com.example.encuestas_api.questions.domain.model.Question;

public interface SetTextSettingsUseCase {
    Question handle(Long questionId, String textMode, String placeholder, Integer minLength, Integer maxLength);
}
