package com.example.encuestas_api.forms.application.port.in;

import com.example.encuestas_api.forms.domain.model.Form;

public interface SetAutoSaveUseCase {
    Form handle(Long formId, boolean enabled);
}
