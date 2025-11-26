package com.example.encuestas_api.responses.application.port.out;

import com.example.encuestas_api.common.dto.PagedResult;
import com.example.encuestas_api.responses.domain.model.Submission;
import com.example.encuestas_api.responses.domain.model.SubmissionStatus;

public interface SearchSubmissionsPort {
    PagedResult<Submission> findByForm(Long formId, SubmissionStatus status, int page, int size);
}
