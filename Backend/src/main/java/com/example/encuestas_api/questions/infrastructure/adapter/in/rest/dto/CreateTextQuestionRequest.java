package com.example.encuestas_api.questions.infrastructure.adapter.in.rest.dto;

import com.example.encuestas_api.common.validation.MinWords;
import com.example.encuestas_api.common.validation.NoWhitespace;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTextQuestionRequest(
        @NotBlank @NoWhitespace @Size(max=500) @MinWords(2) String prompt,
        @Size(max=1000) String helpText,
        boolean required,
        @NotBlank String textMode, 
        @Size(max=300) String placeholder,
        Integer minLength,
        Integer maxLength
) {}
