package com.example.encuestas_api.forms.application.port.out;

import com.example.encuestas_api.forms.domain.model.Section;

import java.util.Optional;

public interface LoadSectionPort {
    Optional<Section> loadById(Long sectionId);
}
