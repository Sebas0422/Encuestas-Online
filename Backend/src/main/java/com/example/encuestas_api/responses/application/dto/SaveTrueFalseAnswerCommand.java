package com.example.encuestas_api.responses.application.dto;

public record SaveTrueFalseAnswerCommand(
        Long submissionId,
        Long questionId,
        boolean value
) {}
