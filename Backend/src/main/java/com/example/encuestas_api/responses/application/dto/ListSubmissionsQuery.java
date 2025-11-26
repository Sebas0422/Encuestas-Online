package com.example.encuestas_api.responses.application.dto;

import com.example.encuestas_api.responses.domain.model.SubmissionStatus;

public record ListSubmissionsQuery(
        Long formId,
        Long respondentUserId,
        SubmissionStatus status,
        int page,
        int size
) {}
