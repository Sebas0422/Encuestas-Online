package com.example.encuestas_api.questions.infrastructure.adapter.in.rest.dto;

import com.example.encuestas_api.common.validation.MinWords;
import com.example.encuestas_api.common.validation.NoWhitespace;
import jakarta.validation.constraints.*;

import java.util.List;

public record CreateChoiceQuestionRequest(
        @NotBlank @NoWhitespace @Size(max=500) @MinWords(2) String prompt,
        @Size(max=1000) String helpText,
        boolean required,
        boolean shuffleOptions,
        @NotBlank String selectionMode,
        Integer minSelections,
        Integer maxSelections,
        @Size(min=2) List<OptionDTO> options
) {
    public record OptionDTO(@NotBlank @Size(max=300) String label, boolean correct) {}
}
