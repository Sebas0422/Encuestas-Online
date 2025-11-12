package com.example.encuestas_api.forms.application.port.in;

import com.example.encuestas_api.forms.domain.model.Form;

public interface SetResponseLimitPolicyUseCase {
    Form handle(Long formId, String mode, Integer n);
}
