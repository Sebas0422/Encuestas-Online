package com.example.encuestas_api.forms.application.port.in;

import com.example.encuestas_api.forms.domain.model.Section;

public interface AddSectionUseCase {
    Section handle(Long formId, String title);
}
