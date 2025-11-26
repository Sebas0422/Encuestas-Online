package com.example.encuestas_api.responses.infrastructure.adapter.in.rest.dto;

import com.example.encuestas_api.common.validation.ValidEmail;
import jakarta.validation.constraints.NotNull;

public record StartSubmissionRequest(
        @NotNull Long formId,
        @NotNull RespondentType respondentType,
        Long userId,
        @ValidEmail String email,
        String code,
        String sourceIp
) {
    public enum RespondentType { ANONYMOUS, USER, EMAIL, CODE }
}
