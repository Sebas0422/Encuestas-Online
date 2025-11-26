package com.example.encuestas_api.questions.application.port.in;

import com.example.encuestas_api.questions.domain.model.Question;

public interface SetMultiBoundsUseCase {
    Question handle(Long questionId, Integer min, Integer max);
}
