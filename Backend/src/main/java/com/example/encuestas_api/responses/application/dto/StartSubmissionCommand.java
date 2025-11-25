package com.example.encuestas_api.responses.application.dto;

public record StartSubmissionCommand(
        Long formId,
        RespondentType respondentType,
        Long userId,
        String email,
        String code,
        String sourceIp
) {
    public enum RespondentType { ANONYMOUS, USER, EMAIL, CODE }
}
