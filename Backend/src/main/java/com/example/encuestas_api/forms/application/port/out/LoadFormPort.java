package com.example.encuestas_api.forms.application.port.out;

import com.example.encuestas_api.forms.domain.model.Form;

import java.util.Optional;

public interface LoadFormPort {
    Optional<Form> loadById(Long formId);
}
