package com.example.encuestas_api.questions.domain.exception;
public class QuestionTypeMismatchException extends RuntimeException {
    public QuestionTypeMismatchException(String message){ super(message); }
}
