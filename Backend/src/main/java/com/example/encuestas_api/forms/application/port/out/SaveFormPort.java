package com.example.encuestas_api.forms.application.port.out;

import com.example.encuestas_api.forms.domain.model.Form;

public interface SaveFormPort {
    Form save(Form form);
}
