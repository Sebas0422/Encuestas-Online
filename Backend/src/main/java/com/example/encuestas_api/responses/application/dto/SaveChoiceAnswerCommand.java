package com.example.encuestas_api.responses.application.dto;

import java.util.Set;

public record SaveChoiceAnswerCommand(
        Long submissionId,
        Long questionId,
        Set<Long> selectedOptionIds
) {}
