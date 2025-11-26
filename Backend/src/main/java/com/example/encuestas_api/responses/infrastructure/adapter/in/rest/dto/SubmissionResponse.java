package com.example.encuestas_api.responses.infrastructure.adapter.in.rest.dto;

import java.time.Instant;

public record SubmissionResponse(
        Long id,
        Long formId,
        String status,
        String respondent,
        String sourceIp,
        Instant createdAt,
        Instant updatedAt,
        Instant submittedAt,
        int answersCount
) {}
