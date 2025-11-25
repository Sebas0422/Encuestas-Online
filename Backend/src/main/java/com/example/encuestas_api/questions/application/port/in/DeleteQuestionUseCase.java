package com.example.encuestas_api.questions.application.port.in;

public interface DeleteQuestionUseCase {
    void handle(Long questionId);
}
