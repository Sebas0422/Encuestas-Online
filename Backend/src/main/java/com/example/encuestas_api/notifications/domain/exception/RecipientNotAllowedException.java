package com.example.encuestas_api.notifications.domain.exception;

public class RecipientNotAllowedException extends RuntimeException {
    public RecipientNotAllowedException(String msg) { super(msg); }
}
