package com.example.encuestas_api.forms.application.port.in;

import com.example.encuestas_api.forms.domain.model.Section;

public interface RenameSectionUseCase {
    Section handle(Long formId, Long sectionId, String newTitle);
}
