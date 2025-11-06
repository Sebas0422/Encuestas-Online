package com.example.encuestas_api.auth.application.port.out;

import com.example.encuestas_api.auth.domain.model.Credential;

public interface SaveCredentialPort {
    Credential save(Credential credential);
}
