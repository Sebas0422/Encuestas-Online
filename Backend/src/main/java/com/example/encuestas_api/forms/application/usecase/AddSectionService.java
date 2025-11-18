package com.example.encuestas_api.forms.application.usecase;

import com.example.encuestas_api.forms.application.port.in.AddSectionUseCase;
import com.example.encuestas_api.forms.application.port.out.ComputeNextSectionPositionPort;
import com.example.encuestas_api.forms.application.port.out.LoadFormPort;
import com.example.encuestas_api.forms.application.port.out.SaveSectionPort;
import com.example.encuestas_api.forms.domain.exception.FormNotFoundException;
import com.example.encuestas_api.forms.domain.model.Section;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AddSectionService implements AddSectionUseCase {

    private final LoadFormPort loadForm;
    private final ComputeNextSectionPositionPort nextPos;
    private final SaveSectionPort saveSection;

    public AddSectionService(LoadFormPort loadForm,
                             ComputeNextSectionPositionPort nextPos,
                             SaveSectionPort saveSection) {
        this.loadForm = loadForm;
        this.nextPos = nextPos;
        this.saveSection = saveSection;
    }

    @Override
    public Section handle(Long formId, String title) {
        loadForm.loadById(formId).orElseThrow(() -> new FormNotFoundException(formId));
        int position = nextPos.nextPositionForForm(formId);
        return saveSection.save(Section.newOf(formId, title, position));
    }
}
