package com.example.encuestas_api.reports.infrastructure.adapter.in.rest.dto;

import java.time.Instant;
import java.util.List;

public record FormReportResponse(
        Long formId,
        long totalSubmissions,
        long submittedCount,
        long draftCount,
        double completionRate,
        Instant generatedAt,
        List<QuestionBlock> questions
) {
    // Polimorfismo de bloques por tipo de pregunta
    public sealed interface QuestionBlock permits ChoiceBlock, TrueFalseBlock, TextBlock, MatchingBlock {}

    // ===== CHOICE =====
    public static final class ChoiceBlock implements QuestionBlock {
        public Long questionId;
        public long answeredCount;
        public long omittedCount;
        public String selectionMode; // "SINGLE" | "MULTI"
        public Integer minSelections;
        public Integer maxSelections;
        public List<OptionStat> options;

        public ChoiceBlock(Long questionId, long answeredCount, long omittedCount,
                           String selectionMode, Integer minSelections, Integer maxSelections,
                           List<OptionStat> options) {
            this.questionId = questionId;
            this.answeredCount = answeredCount;
            this.omittedCount = omittedCount;
            this.selectionMode = selectionMode;
            this.minSelections = minSelections;
            this.maxSelections = maxSelections;
            this.options = options;
        }

        public static final class OptionStat {
            public Long optionId;
            public long count;
            public OptionStat(Long optionId, long count) { this.optionId = optionId; this.count = count; }
        }
    }

    // ===== TRUE/FALSE =====
    public static final class TrueFalseBlock implements QuestionBlock {
        public Long questionId;
        public long answeredCount;
        public long omittedCount;
        public long trueCount;
        public long falseCount;

        public TrueFalseBlock(Long questionId, long answeredCount, long omittedCount, long trueCount, long falseCount) {
            this.questionId = questionId; this.answeredCount = answeredCount; this.omittedCount = omittedCount;
            this.trueCount = trueCount; this.falseCount = falseCount;
        }
    }

    // ===== TEXT =====
    public static final class TextBlock implements QuestionBlock {
        public Long questionId;
        public long answeredCount;
        public long omittedCount;
        public String textMode; // "SHORT" | "LONG"
        public Integer minLength;
        public Integer maxLength;

        public TextBlock(Long questionId, long answeredCount, long omittedCount,
                         String textMode, Integer minLength, Integer maxLength) {
            this.questionId = questionId; this.answeredCount = answeredCount; this.omittedCount = omittedCount;
            this.textMode = textMode; this.minLength = minLength; this.maxLength = maxLength;
        }
    }

    // ===== MATCHING =====
    public static final class MatchingBlock implements QuestionBlock {
        public Long questionId;
        public long answeredCount;
        public long omittedCount;
        public List<Long> leftIds;
        public List<Long> rightIds;
        public List<PairStat> pairs;

        public MatchingBlock(Long questionId, long answeredCount, long omittedCount,
                             List<Long> leftIds, List<Long> rightIds, List<PairStat> pairs) {
            this.questionId = questionId; this.answeredCount = answeredCount; this.omittedCount = omittedCount;
            this.leftIds = leftIds; this.rightIds = rightIds; this.pairs = pairs;
        }

        public static final class PairStat {
            public Long leftId; public Long rightId; public long count;
            public PairStat(Long leftId, Long rightId, long count) { this.leftId = leftId; this.rightId = rightId; this.count = count; }
        }
    }
}
