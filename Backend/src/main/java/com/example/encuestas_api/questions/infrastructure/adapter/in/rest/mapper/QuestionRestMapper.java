package com.example.encuestas_api.questions.infrastructure.adapter.in.rest.mapper;

import com.example.encuestas_api.questions.domain.model.*;
import com.example.encuestas_api.questions.infrastructure.adapter.in.rest.dto.QuestionResponse;

import java.util.List;
import java.util.Map;

public final class QuestionRestMapper {
    private QuestionRestMapper(){}

    public static QuestionResponse toResponse(Question q) {
        List<QuestionResponse.OptionRes> options = null;
        String selectionMode = null; Integer minSel = null; Integer maxSel = null;
        String textMode = null; String placeholder = null; Integer minLen = null; Integer maxLen = null;
        List<QuestionResponse.MatchingItemRes> left = null; List<QuestionResponse.MatchingItemRes> right = null;
        Map<Long, Long> key = null;

        switch (q.getType()) {
            case CHOICE, TRUE_FALSE -> {
                var cs = q.getChoice();
                selectionMode = cs.mode().name();
                minSel = cs.minSelections(); maxSel = cs.maxSelections();
                options = cs.options().stream()
                        .map(o -> new QuestionResponse.OptionRes(o.id(), o.label(), o.correct()))
                        .toList();
            }
            case TEXT -> {
                var ts = q.getText();
                textMode = ts.mode().name();
                placeholder = ts.placeholder();
                minLen = ts.minLength(); maxLen = ts.maxLength();
            }
            case MATCHING -> {
                var ms = q.getMatching();
                left = ms.left().stream().map(i -> new QuestionResponse.MatchingItemRes(i.id(), i.text())).toList();
                right = ms.right().stream().map(i -> new QuestionResponse.MatchingItemRes(i.id(), i.text())).toList();
                key = ms.answerKey();
            }
        }

        return new QuestionResponse(
                q.getId(), q.getFormId(), q.getSectionId(), q.getPosition(), q.getType(),
                q.getPrompt().getValue(), q.getHelpText(), q.isRequired(), q.isShuffleOptions(),
                selectionMode, minSel, maxSel, options,
                textMode, placeholder, minLen, maxLen,
                left, right, key, q.getCreatedAt(), q.getUpdatedAt()
        );
    }
}
