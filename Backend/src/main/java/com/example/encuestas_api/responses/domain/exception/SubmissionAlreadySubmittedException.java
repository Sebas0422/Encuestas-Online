package com.example.encuestas_api.responses.domain.exception;

public class SubmissionAlreadySubmittedException extends RuntimeException {
    public SubmissionAlreadySubmittedException(String message) { super(message); }
}
