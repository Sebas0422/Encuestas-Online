package com.example.encuestas_api.forms.infrastructure.adapter.in.rest.dto;

import com.example.encuestas_api.common.validation.MinWords;
import com.example.encuestas_api.common.validation.NoWhitespace;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record CreateFormRequest(
        @NotNull Long campaignId,
        @NotBlank @NoWhitespace @Size(max = 200) String title,
        @Size(max = 2000) @MinWords(2) String description,
        String coverUrl,
        String themeMode,          
        String themePrimary,
        String accessMode,       
        Instant openAt,
        Instant closeAt,
        String responseLimitMode,  
        Integer limitedN,
        Boolean anonymousMode,
        Boolean allowEditBeforeSubmit,
        Boolean autoSave,
        Boolean shuffleQuestions,
        Boolean shuffleOptions,
        Boolean progressBar,
        Boolean paginated
) { }
