package com.example.encuestas_api.forms.application.port.in;

import com.example.encuestas_api.forms.domain.model.Form;
import com.example.encuestas_api.forms.domain.model.FormStatus;

public interface ChangeFormStatusUseCase {
    Form handle(Long formId, FormStatus target);
}
