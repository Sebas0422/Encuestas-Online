package com.example.encuestas_api.forms.application.dto;

public record SectionListQuery(
        Long formId,
        int page,
        int size
) {
    public SectionListQuery {
        if (formId == null) throw new IllegalArgumentException("formId requerido");
        if (page < 0) throw new IllegalArgumentException("page debe ser >= 0");
        if (size <= 0 || size > 200) throw new IllegalArgumentException("size inválido");
    }
}
