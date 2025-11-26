package com.example.encuestas_api.questions.application.port.in;

import com.example.encuestas_api.questions.domain.model.Question;

public interface RenameQuestionUseCase {
    Question handle(Long questionId, String newPrompt);
}
