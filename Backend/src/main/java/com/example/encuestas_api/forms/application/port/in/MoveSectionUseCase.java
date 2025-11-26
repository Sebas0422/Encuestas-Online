package com.example.encuestas_api.forms.application.port.in;

import com.example.encuestas_api.forms.domain.model.Section;

public interface MoveSectionUseCase {
    Section handle(Long formId, Long sectionId, int newPosition);
}
