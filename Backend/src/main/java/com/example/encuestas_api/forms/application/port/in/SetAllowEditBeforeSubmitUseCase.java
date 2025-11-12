package com.example.encuestas_api.forms.application.port.in;

import com.example.encuestas_api.forms.domain.model.Form;

public interface SetAllowEditBeforeSubmitUseCase {
    Form handle(Long formId, boolean allow);
}
