package com.example.encuestas_api.questions.application.port.out;

import com.example.encuestas_api.common.dto.PagedResult;
import com.example.encuestas_api.questions.application.dto.QuestionListQuery;
import com.example.encuestas_api.questions.domain.model.Question;

public interface SearchQuestionsPort {
    PagedResult<Question> search(QuestionListQuery query);
}
