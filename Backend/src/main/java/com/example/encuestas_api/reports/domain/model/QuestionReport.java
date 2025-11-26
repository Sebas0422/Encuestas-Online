package com.example.encuestas_api.reports.domain.model;

import com.example.encuestas_api.responses.domain.valueobject.QuestionKind;

public abstract class QuestionReport {
    private final Long questionId;
    private final QuestionKind kind;
    private final long answeredCount;
    private final long omittedCount;

    protected QuestionReport(Long questionId, QuestionKind kind, long answeredCount, long omittedCount) {
        this.questionId = questionId;
        this.kind = kind;
        this.answeredCount = answeredCount;
        this.omittedCount = omittedCount;
    }

    public Long getQuestionId() { return questionId; }
    public QuestionKind getKind() { return kind; }
    public long getAnsweredCount() { return answeredCount; }
    public long getOmittedCount() { return omittedCount; }
}
