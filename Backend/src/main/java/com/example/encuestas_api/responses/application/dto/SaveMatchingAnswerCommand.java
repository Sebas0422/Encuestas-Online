package com.example.encuestas_api.responses.application.dto;

import java.util.List;

public record SaveMatchingAnswerCommand(
        Long submissionId,
        Long questionId,
        List<Pair> pairs
) {
    public record Pair(Long leftId, Long rightId) {}
}
