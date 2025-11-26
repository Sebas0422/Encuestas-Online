package com.example.encuestas_api.responses.domain.exception;

public class InvalidAnswerException extends RuntimeException {
    private final Long questionId;
    private final String code;

    public InvalidAnswerException(Long questionId, String message) {
        super(message);
        this.questionId = questionId;
        this.code = null;
    }

    public InvalidAnswerException(Long questionId, String code, String message) {
        super(message);
        this.questionId = questionId;
        this.code = code;
    }

    public Long getQuestionId() { return questionId; }
    public String getCode() { return code; }
}
