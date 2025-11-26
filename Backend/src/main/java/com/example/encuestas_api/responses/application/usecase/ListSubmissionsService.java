package com.example.encuestas_api.responses.application.usecase;

import com.example.encuestas_api.common.dto.PagedResult;
import com.example.encuestas_api.responses.application.dto.ListSubmissionsQuery;
import com.example.encuestas_api.responses.application.port.in.ListSubmissionsUseCase;
import com.example.encuestas_api.responses.application.port.out.SearchSubmissionsPort;
import com.example.encuestas_api.responses.domain.model.Submission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ListSubmissionsService implements ListSubmissionsUseCase {

    private final SearchSubmissionsPort searchPort;

    public ListSubmissionsService(SearchSubmissionsPort searchPort) {
        this.searchPort = searchPort;
    }

    @Override
    public PagedResult<Submission> handle(ListSubmissionsQuery q) {
        return searchPort.findByForm(q.formId(), q.status(), q.page(), q.size());
    }
}
