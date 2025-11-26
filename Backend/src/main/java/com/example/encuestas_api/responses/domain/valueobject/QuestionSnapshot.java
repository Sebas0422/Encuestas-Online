package com.example.encuestas_api.responses.domain.valueobject;

import com.example.encuestas_api.questions.domain.model.TextMode;

import java.util.*;

public class QuestionSnapshot {
    private final Long questionId;
    private final QuestionKind kind;
    private final boolean required;

    private final SelectionMode selectionMode;
    private final Integer minSelections;
    private final Integer maxSelections;
    private final Set<Long> optionIds;

    private final TextMode textMode;
    private final Integer minLength;
    private final Integer maxLength;

    private final Set<Long> leftIds;
    private final Set<Long> rightIds;

    private QuestionSnapshot(Builder b) {
        this.questionId = Objects.requireNonNull(b.questionId, "questionId");
        this.kind = Objects.requireNonNull(b.kind, "kind");
        this.required = b.required;

        this.selectionMode = b.selectionMode;
        this.minSelections = b.minSelections;
        this.maxSelections = b.maxSelections;
        this.optionIds = Collections.unmodifiableSet(new LinkedHashSet<>(b.optionIds));

        this.textMode = b.textMode;
        this.minLength = b.minLength;
        this.maxLength = b.maxLength;

        this.leftIds = Collections.unmodifiableSet(new LinkedHashSet<>(b.leftIds));
        this.rightIds = Collections.unmodifiableSet(new LinkedHashSet<>(b.rightIds));
    }

    public Long getQuestionId() { return questionId; }
    public QuestionKind getKind() { return kind; }
    public boolean isRequired() { return required; }

    public SelectionMode getSelectionMode() { return selectionMode; }
    public Integer getMinSelections() { return minSelections; }
    public Integer getMaxSelections() { return maxSelections; }
    public Set<Long> getOptionIds() { return optionIds; }

    public TextMode getTextMode() { return textMode; }
    public Integer getMinLength() { return minLength; }
    public Integer getMaxLength() { return maxLength; }

    public Set<Long> getLeftIds() { return leftIds; }
    public Set<Long> getRightIds() { return rightIds; }

    public static class Builder {
        private Long questionId;
        private QuestionKind kind;
        private boolean required;

        private SelectionMode selectionMode;
        private Integer minSelections;
        private Integer maxSelections;
        private final Set<Long> optionIds = new LinkedHashSet<>();

        private TextMode textMode;
        private Integer minLength;
        private Integer maxLength;

        private final Set<Long> leftIds = new LinkedHashSet<>();
        private final Set<Long> rightIds = new LinkedHashSet<>();

        public Builder questionId(Long id) { this.questionId = id; return this; }
        public Builder kind(QuestionKind k) { this.kind = k; return this; }
        public Builder required(boolean r) { this.required = r; return this; }

        public Builder choice(SelectionMode mode, Integer min, Integer max, Collection<Long> options) {
            this.selectionMode = mode;
            this.minSelections = min;
            this.maxSelections = max;
            if (options != null) this.optionIds.addAll(options);
            return this;
        }

        public Builder text(TextMode mode, Integer minLen, Integer maxLen) {
            this.textMode = mode;
            this.minLength = minLen;
            this.maxLength = maxLen;
            return this;
        }

        public Builder matching(Collection<Long> left, Collection<Long> right) {
            if (left  != null) this.leftIds.addAll(left);
            if (right != null) this.rightIds.addAll(right);
            return this;
        }

        public QuestionSnapshot build() { return new QuestionSnapshot(this); }
    }
}
