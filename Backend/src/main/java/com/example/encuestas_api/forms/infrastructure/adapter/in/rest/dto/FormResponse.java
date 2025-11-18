package com.example.encuestas_api.forms.infrastructure.adapter.in.rest.dto;

import com.example.encuestas_api.forms.domain.model.AccessMode;
import com.example.encuestas_api.forms.domain.model.FormStatus;

import java.time.Instant;

public record FormResponse(
        Long id,
        Long campaignId,
        String title,
        String description,
        String coverUrl,
        String themeMode,
        String themePrimary,
        AccessMode accessMode,
        Instant openAt,
        Instant closeAt,
        String limitMode,
        Integer limitN,
        boolean anonymousMode,
        boolean allowEditBeforeSubmit,
        boolean autoSave,
        boolean shuffleQuestions,
        boolean shuffleOptions,
        boolean progressBar,
        boolean paginated,
        FormStatus status,
        Instant createdAt,
        Instant updatedAt
) {}
