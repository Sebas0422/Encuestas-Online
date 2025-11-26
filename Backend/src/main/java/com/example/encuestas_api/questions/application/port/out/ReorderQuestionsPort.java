package com.example.encuestas_api.questions.application.port.out;

import com.example.encuestas_api.questions.domain.model.Question;

public interface ReorderQuestionsPort {
    Question moveTo(Long questionId, Long newSectionId, int newPosition);
}
