package com.example.encuestas_api.responses.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record SaveChoiceAnswerRequest(
        @NotNull Long questionId,
        @Size(min = 0) Set<Long> selectedOptionIds
) {}
