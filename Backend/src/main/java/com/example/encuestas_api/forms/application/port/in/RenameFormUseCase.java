package com.example.encuestas_api.forms.application.port.in;

import com.example.encuestas_api.forms.domain.model.Form;

public interface RenameFormUseCase {
    Form handle(Long formId, String newTitle);
}
