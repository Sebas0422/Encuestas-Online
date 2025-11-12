package com.example.encuestas_api.forms.application.port.in;

public interface DeleteSectionUseCase {
    void handle(Long formId, Long sectionId);
}
