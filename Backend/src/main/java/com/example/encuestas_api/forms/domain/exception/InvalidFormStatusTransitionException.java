package com.example.encuestas_api.forms.domain.exception;

import com.example.encuestas_api.forms.domain.model.FormStatus;

public class InvalidFormStatusTransitionException extends RuntimeException {
    public InvalidFormStatusTransitionException(FormStatus from, FormStatus to){
        super("Transición de estado inválida: "+from+" → "+to);
    }
}
