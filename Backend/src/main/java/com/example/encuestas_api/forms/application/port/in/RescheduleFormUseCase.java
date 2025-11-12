package com.example.encuestas_api.forms.application.port.in;

import com.example.encuestas_api.forms.domain.model.Form;

import java.time.Instant;

public interface RescheduleFormUseCase {
    Form handle(Long formId, Instant openAt, Instant closeAt);
}
