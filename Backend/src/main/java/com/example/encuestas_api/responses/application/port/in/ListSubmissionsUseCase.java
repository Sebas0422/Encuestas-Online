package com.example.encuestas_api.responses.application.port.in;

import com.example.encuestas_api.common.dto.PagedResult;
import com.example.encuestas_api.responses.application.dto.ListSubmissionsQuery;
import com.example.encuestas_api.responses.domain.model.Submission;

public interface ListSubmissionsUseCase {
    PagedResult<Submission> handle(ListSubmissionsQuery q);
}
