package com.example.encuestas_api.forms.domain.exception;
public class FormAlreadyExistsException extends RuntimeException {
    public FormAlreadyExistsException(String name){ super("Ya existe un form con título '"+name+"' en la campaña"); }
}
