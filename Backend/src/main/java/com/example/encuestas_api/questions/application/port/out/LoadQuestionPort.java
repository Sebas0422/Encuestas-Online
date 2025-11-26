package com.example.encuestas_api.questions.application.port.out;

import com.example.encuestas_api.questions.domain.model.Question;

import java.util.Optional;

public interface LoadQuestionPort {
    Optional<Question> loadById(Long id);
}
