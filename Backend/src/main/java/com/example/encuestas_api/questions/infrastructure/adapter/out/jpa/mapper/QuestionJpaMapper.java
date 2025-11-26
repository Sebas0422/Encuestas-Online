package com.example.encuestas_api.questions.infrastructure.adapter.out.jpa.mapper;

import com.example.encuestas_api.questions.domain.model.*;
import com.example.encuestas_api.questions.domain.valueobject.Prompt;
import com.example.encuestas_api.questions.infrastructure.adapter.out.jpa.entity.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class QuestionJpaMapper {
    private QuestionJpaMapper(){}

    public static Question toDomain(QuestionEntity e,
                                    List<OptionEntity> options,
                                    List<MatchingLeftEntity> left,
                                    List<MatchingRightEntity> right,
                                    List<MatchingPairEntity> pairs) {
        ChoiceSettings choice = null;
        TextSettings text = null;
        MatchingSettings matching = null;

        switch (e.getType()) {
            case CHOICE, TRUE_FALSE -> {
                List<Option> opts = new ArrayList<>();
                for (var oe : options) {
                    opts.add(Option.rehydrate(oe.getId(), oe.getLabel(), oe.isCorrect()));
                }
                if ("MULTI".equalsIgnoreCase(e.getSelectionMode())) {
                    choice = ChoiceSettings.multi(opts, e.getMinSelections(), e.getMaxSelections());
                } else {
                    choice = ChoiceSettings.single(opts);
                }
            }
            case TEXT -> {
                TextMode mode = e.getTextMode() == null ? TextMode.SHORT : TextMode.valueOf(e.getTextMode());
                text = TextSettings.of(mode, e.getPlaceholder(), e.getMinLength(), e.getMaxLength());
            }
            case MATCHING -> {
                var l = new ArrayList<MatchingItem>();
                var r = new ArrayList<MatchingItem>();
                for (var le : left) l.add(MatchingItem.rehydrate(le.getId(), le.getText()));
                for (var re : right) r.add(MatchingItem.rehydrate(re.getId(), re.getText()));
                var map = new java.util.HashMap<Long, Long>();
                for (var p : pairs) map.put(p.getLeftId(), p.getRightId());
                matching = MatchingSettings.of(l, r, map);
            }
        }

        Instant created = e.getCreatedAt();
        Instant updated = e.getUpdatedAt();

        return Question.rehydrate(
                e.getId(), e.getFormId(), e.getSectionId(), e.getPosition(),
                e.getType(), Prompt.of(e.getPrompt()), e.getHelpText(), e.isRequired(), e.isShuffleOptions(),
                choice, text, matching, created, updated
        );
    }

    public static void fillEntityFromDomain(Question d, QuestionEntity e) {
        e.setId(d.getId());
        e.setFormId(d.getFormId());
        e.setSectionId(d.getSectionId());
        e.setPosition(d.getPosition());
        e.setType(d.getType());
        e.setPrompt(d.getPrompt().getValue());
        e.setHelpText(d.getHelpText());
        e.setRequired(d.isRequired());
        e.setShuffleOptions(d.isShuffleOptions());
        e.setCreatedAt(d.getCreatedAt());
        e.setUpdatedAt(d.getUpdatedAt());

        // Reset type-specific
        e.setSelectionMode(null); e.setMinSelections(null); e.setMaxSelections(null);
        e.setTextMode(null); e.setPlaceholder(null); e.setMinLength(null); e.setMaxLength(null);

        switch (d.getType()) {
            case CHOICE, TRUE_FALSE -> {
                var cs = d.getChoice();
                e.setSelectionMode(cs.mode().name());
                if (cs.mode() == SelectionMode.MULTI) {
                    e.setMinSelections(cs.minSelections());
                    e.setMaxSelections(cs.maxSelections());
                }
            }
            case TEXT -> {
                var ts = d.getText();
                e.setTextMode(ts.mode().name());
                e.setPlaceholder(ts.placeholder());
                e.setMinLength(ts.minLength());
                e.setMaxLength(ts.maxLength());
            }
            case MATCHING -> {
                // Persistencia de pares se maneja fuera (adapter)
            }
        }
    }

    public static List<OptionEntity> toOptionEntities(Long questionId, List<Option> options) {
        var list = new ArrayList<OptionEntity>();
        int pos = 0;
        for (var o : options) {
            var e = new OptionEntity();
            e.setQuestionId(questionId);
            e.setLabel(o.label());
            e.setCorrect(o.correct());
            e.setPosition(pos++);
            list.add(e);
        }
        return list;
    }

    public static List<MatchingPairEntity> toPairEntities(Long questionId, Map<Long, Long> key) {
        var list = new ArrayList<MatchingPairEntity>();
        for (var entry : key.entrySet()) {
            var e = new MatchingPairEntity();
            e.setQuestionId(questionId);
            e.setLeftId(entry.getKey());
            e.setRightId(entry.getValue());
            list.add(e);
        }
        return list;
    }
}
