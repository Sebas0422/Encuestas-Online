package com.example.encuestas_api.forms.application.dto;

import com.example.encuestas_api.forms.domain.model.AccessMode;
import com.example.encuestas_api.forms.domain.model.FormStatus;

import java.time.Instant;

public record FormListQuery(
        Long campaignId,
        String search,
        FormStatus status,
        AccessMode accessMode,
        Instant openFrom,
        Instant closeTo,
        int page,
        int size
) {
    public FormListQuery {
        if (page < 0) throw new IllegalArgumentException("page debe ser >= 0");
        if (size <= 0 || size > 200) throw new IllegalArgumentException("size inválido");
    }
}
