package com.example.encuestas_api.questions.application.port.out;

public interface CheckSectionBelongsToFormPort {
    boolean belongs(Long formId, Long sectionId);
}
