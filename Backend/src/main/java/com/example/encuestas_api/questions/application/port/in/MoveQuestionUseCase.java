package com.example.encuestas_api.questions.application.port.in;

import com.example.encuestas_api.questions.domain.model.Question;

public interface MoveQuestionUseCase {
    Question handle(Long questionId, Long targetSectionId, int newPosition);
}
