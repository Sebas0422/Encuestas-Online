package com.example.encuestas_api.forms.application.usecase;

import com.example.encuestas_api.forms.application.port.in.MoveSectionUseCase;
import com.example.encuestas_api.forms.application.port.out.LoadSectionPort;
import com.example.encuestas_api.forms.application.port.out.ReorderSectionsPort;
import com.example.encuestas_api.forms.domain.model.Section;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MoveSectionService implements MoveSectionUseCase {

    private final LoadSectionPort loadSection;
    private final ReorderSectionsPort reorder;

    public MoveSectionService(LoadSectionPort loadSection, ReorderSectionsPort reorder) {
        this.loadSection = loadSection; this.reorder = reorder;
    }

    @Override
    public Section handle(Long formId, Long sectionId, int newPosition) {
        var s = loadSection.loadById(sectionId).orElseThrow();
        if (!s.getFormId().equals(formId)) throw new IllegalArgumentException("La sección no pertenece al form");
        if (newPosition < 0) throw new IllegalArgumentException("newPosition >= 0 requerido");
        return reorder.moveTo(formId, sectionId, newPosition);
    }
}
