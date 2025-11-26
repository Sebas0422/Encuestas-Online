package com.example.encuestas_api.forms.application.port.out;

public interface DeleteSectionPort {
    void delete(Long formId, Long sectionId);
}
