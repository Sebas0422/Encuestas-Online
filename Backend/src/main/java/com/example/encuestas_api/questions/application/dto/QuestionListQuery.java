package com.example.encuestas_api.questions.application.dto;

import com.example.encuestas_api.questions.domain.model.QuestionType;

public record QuestionListQuery(
        Long formId,
        Long sectionId,
        QuestionType type,
        String search,
        int page,
        int size
) {
    public QuestionListQuery {
        if (formId == null) throw new IllegalArgumentException("formId requerido");
        if (page < 0) throw new IllegalArgumentException("page debe ser >= 0");
        if (size <= 0 || size > 200) throw new IllegalArgumentException("size inválido");
    }
}
