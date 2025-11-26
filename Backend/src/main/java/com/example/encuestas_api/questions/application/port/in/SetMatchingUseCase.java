package com.example.encuestas_api.questions.application.port.in;

import com.example.encuestas_api.questions.domain.model.Question;

import java.util.Map;

public interface SetMatchingUseCase {
    Question handle(Long questionId, Map<Long, Long> key);
}
