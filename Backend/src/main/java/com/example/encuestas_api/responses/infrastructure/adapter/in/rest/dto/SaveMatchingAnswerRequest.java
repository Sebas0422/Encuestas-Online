package com.example.encuestas_api.responses.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record SaveMatchingAnswerRequest(
        @NotNull Long questionId,
        List<Pair> pairs
) {
    public record Pair(Long leftId, Long rightId) {}
}
