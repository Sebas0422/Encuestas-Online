package com.example.encuestas_api.questions.infrastructure.adapter.in.rest.dto;

import com.example.encuestas_api.common.validation.MinWords;
import com.example.encuestas_api.common.validation.NoWhitespace;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateMatchingQuestionRequest(
        @NotBlank @NoWhitespace @Size(max=500) @MinWords(2) String prompt,
        @Size(max=1000) String helpText,
        boolean required,
        boolean shuffleRightColumn,
        @Size(min=1) List<@NotBlank @Size(max=300) String> leftTexts,
        @Size(min=1) List<@NotBlank @Size(max=300) String> rightTexts,
        @Size(min=1) List<PairIdx> keyPairs
) {
    public record PairIdx(int leftIndex, int rightIndex) {}
}
