package com.example.encuestas_api.forms.application.port.out;

import com.example.encuestas_api.forms.domain.model.Section;

public interface ReorderSectionsPort {
    Section moveTo(Long formId, Long sectionId, int newPosition);
}
