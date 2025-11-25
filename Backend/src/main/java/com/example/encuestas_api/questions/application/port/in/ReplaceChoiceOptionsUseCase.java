package com.example.encuestas_api.questions.application.port.in;

import com.example.encuestas_api.questions.domain.model.Question;

import java.util.List;

public interface ReplaceChoiceOptionsUseCase {
    Question handle(Long questionId, List<CreateChoiceQuestionUseCase.OptionCmd> options);
}
