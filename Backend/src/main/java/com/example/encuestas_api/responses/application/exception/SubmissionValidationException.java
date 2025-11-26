package com.example.encuestas_api.responses.application.exception;

import com.example.encuestas_api.responses.domain.exception.InvalidAnswerException;

import java.util.List;

public class SubmissionValidationException extends RuntimeException {
    private final List<InvalidAnswerException> errors;

    public SubmissionValidationException(List<InvalidAnswerException> errors) {
        super("La submission contiene respuestas inválidas");
        this.errors = errors;
    }

    public List<InvalidAnswerException> getErrors() { return errors; }
}
