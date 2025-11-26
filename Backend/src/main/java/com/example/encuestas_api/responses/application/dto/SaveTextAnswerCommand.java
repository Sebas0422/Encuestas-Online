package com.example.encuestas_api.responses.application.dto;

public record SaveTextAnswerCommand(
        Long submissionId,
        Long questionId,
        String text
) {}
