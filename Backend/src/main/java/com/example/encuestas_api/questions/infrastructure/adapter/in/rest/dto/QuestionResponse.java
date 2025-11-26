package com.example.encuestas_api.questions.infrastructure.adapter.in.rest.dto;

import com.example.encuestas_api.questions.domain.model.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record QuestionResponse(
        Long id,
        Long formId,
        Long sectionId,
        int position,
        QuestionType type,
        String prompt,
        String helpText,
        boolean required,
        boolean shuffleOptions,
        String selectionMode,
        Integer minSelections,
        Integer maxSelections,
        List<OptionRes> options,
        String textMode,
        String placeholder,
        Integer minLength,
        Integer maxLength,
        List<MatchingItemRes> left,
        List<MatchingItemRes> right,
        Map<Long, Long> answerKey,
        Instant createdAt,
        Instant updatedAt
) {
    public record OptionRes(Long id, String label, boolean correct) {}
    public record MatchingItemRes(Long id, String text) {}
}
