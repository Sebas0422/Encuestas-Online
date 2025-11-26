package com.example.encuestas_api.questions.application.port.in;

import com.example.encuestas_api.questions.domain.model.Question;

public interface GetQuestionUseCase {
    Question handle(Long questionId);
}
