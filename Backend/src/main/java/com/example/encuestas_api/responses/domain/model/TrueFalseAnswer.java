package com.example.encuestas_api.responses.domain.model;

public class TrueFalseAnswer extends SubmissionAnswer {

    private final boolean value;

    public TrueFalseAnswer(Long questionId, Integer questionVersion, boolean value) {
        super(questionId, questionVersion);
        this.value = value;
    }

    public boolean isValue() { return value; }
}
