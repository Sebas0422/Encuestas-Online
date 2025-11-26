package com.example.encuestas_api.questions.application.usecase;

import com.example.encuestas_api.common.dto.PagedResult;
import com.example.encuestas_api.questions.application.dto.QuestionListQuery;
import com.example.encuestas_api.questions.application.port.in.ListQuestionsUseCase;
import com.example.encuestas_api.questions.application.port.out.SearchQuestionsPort;
import com.example.encuestas_api.questions.domain.model.Question;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ListQuestionsService implements ListQuestionsUseCase {
    private final SearchQuestionsPort searchPort;
    public ListQuestionsService(SearchQuestionsPort searchPort){ this.searchPort = searchPort; }
    @Override public PagedResult<Question> handle(QuestionListQuery query){ return searchPort.search(query); }
}
