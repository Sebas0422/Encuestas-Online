package com.example.encuestas_api.forms.domain.exception;
public class FormNotFoundException extends RuntimeException {
    public FormNotFoundException(Long id){ super("Form id="+id+" no encontrado"); }
}
