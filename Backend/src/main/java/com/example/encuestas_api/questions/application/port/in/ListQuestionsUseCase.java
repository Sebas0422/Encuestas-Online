package com.example.encuestas_api.questions.application.port.in;

import com.example.encuestas_api.common.dto.PagedResult;
import com.example.encuestas_api.questions.application.dto.QuestionListQuery;
import com.example.encuestas_api.questions.domain.model.Question;

public interface ListQuestionsUseCase {
    PagedResult<Question> handle(QuestionListQuery query);
}
