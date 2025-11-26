package com.example.encuestas_api.questions.application.port.out;

import com.example.encuestas_api.questions.domain.model.Question;

public interface SaveQuestionPort {
    Question save(Question question);
}
