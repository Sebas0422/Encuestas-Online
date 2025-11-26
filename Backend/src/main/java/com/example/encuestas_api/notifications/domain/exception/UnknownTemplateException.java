package com.example.encuestas_api.notifications.domain.exception;

public class UnknownTemplateException extends RuntimeException {
    public UnknownTemplateException(String code) { super("Plantilla no encontrada: " + code); }
}
