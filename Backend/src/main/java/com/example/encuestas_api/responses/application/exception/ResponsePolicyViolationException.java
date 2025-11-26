package com.example.encuestas_api.responses.application.exception;

public class ResponsePolicyViolationException extends RuntimeException {
    public ResponsePolicyViolationException(String message) { super(message); }
}
