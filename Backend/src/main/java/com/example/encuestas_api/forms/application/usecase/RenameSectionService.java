package com.example.encuestas_api.forms.application.usecase;

import com.example.encuestas_api.forms.application.port.in.RenameSectionUseCase;
import com.example.encuestas_api.forms.application.port.out.LoadSectionPort;
import com.example.encuestas_api.forms.application.port.out.SaveSectionPort;
import com.example.encuestas_api.forms.domain.model.Section;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RenameSectionService implements RenameSectionUseCase {

    private final LoadSectionPort loadSection;
    private final SaveSectionPort saveSection;

    public RenameSectionService(LoadSectionPort loadSection, SaveSectionPort saveSection) {
        this.loadSection = loadSection; this.saveSection = saveSection;
    }

    @Override
    public Section handle(Long formId, Long sectionId, String newTitle) {
        var s = loadSection.loadById(sectionId).orElseThrow();
        if (!s.getFormId().equals(formId)) throw new IllegalArgumentException("La sección no pertenece al form");
        var updated = Section.rehydrate(s.getId(), s.getFormId(), newTitle, s.getPosition());
        return saveSection.save(updated);
    }
}
