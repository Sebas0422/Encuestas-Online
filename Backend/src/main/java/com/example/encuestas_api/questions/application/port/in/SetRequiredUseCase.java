package com.example.encuestas_api.questions.application.port.in;

import com.example.encuestas_api.questions.domain.model.Question;

public interface SetRequiredUseCase {
    Question handle(Long questionId, boolean required);
}
