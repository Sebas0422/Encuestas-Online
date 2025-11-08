package com.example.encuestas_api.forms.application.port.in;

import com.example.encuestas_api.forms.domain.model.Form;

public interface GetFormUseCase {
    Form handle(Long formId);
}
