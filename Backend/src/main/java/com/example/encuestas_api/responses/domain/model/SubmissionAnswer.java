package com.example.encuestas_api.responses.domain.model;

import java.util.Objects;

public abstract class SubmissionAnswer {
    private final Long questionId;
    private final Integer questionVersion;

    protected SubmissionAnswer(Long questionId, Integer questionVersion) {
        this.questionId = Objects.requireNonNull(questionId, "questionId");
        this.questionVersion = questionVersion;
    }

    public Long getQuestionId() { return questionId; }
    public Integer getQuestionVersion() { return questionVersion; }
}
