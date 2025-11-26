package com.example.encuestas_api.reports.domain.model;

import com.example.encuestas_api.questions.domain.model.TextMode;
import com.example.encuestas_api.responses.domain.valueobject.QuestionKind;

public class TextQuestionReport extends QuestionReport {
    private final TextMode textMode;
    private final Integer minLength;
    private final Integer maxLength;

    public TextQuestionReport(Long questionId,
                              long answeredCount,
                              long omittedCount,
                              TextMode textMode,
                              Integer minLength,
                              Integer maxLength) {
        super(questionId, QuestionKind.TEXT, answeredCount, omittedCount);
        this.textMode = textMode;
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    public TextMode getTextMode() { return textMode; }
    public Integer getMinLength() { return minLength; }
    public Integer getMaxLength() { return maxLength; }
}
