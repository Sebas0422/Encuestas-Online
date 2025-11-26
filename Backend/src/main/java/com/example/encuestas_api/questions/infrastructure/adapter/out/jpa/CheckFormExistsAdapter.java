package com.example.encuestas_api.questions.infrastructure.adapter.out.jpa;

import com.example.encuestas_api.forms.infrastructure.adapter.out.jpa.repository.FormJpaRepository;
import com.example.encuestas_api.forms.infrastructure.adapter.out.jpa.repository.SectionJpaRepository;
import com.example.encuestas_api.questions.application.port.out.CheckFormExistsPort;
import com.example.encuestas_api.questions.application.port.out.CheckSectionBelongsToFormPort;
import org.springframework.stereotype.Component;

@Component
class CheckFormExistsAdapter implements CheckFormExistsPort {
    private final FormJpaRepository forms;
    public CheckFormExistsAdapter(FormJpaRepository forms){ this.forms = forms; }
    @Override public boolean exists(Long formId) { return formId != null && forms.existsById(formId); }
}

@Component
class CheckSectionBelongsToFormAdapter implements CheckSectionBelongsToFormPort {
    private final SectionJpaRepository sections;
    public CheckSectionBelongsToFormAdapter(SectionJpaRepository sections){ this.sections = sections; }
    @Override public boolean belongs(Long formId, Long sectionId) {
        if (sectionId == null) return true;
        return sections.findById(sectionId).map(s -> s.getFormId().equals(formId)).orElse(false);
    }
}
