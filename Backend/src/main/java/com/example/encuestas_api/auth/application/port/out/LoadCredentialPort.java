package com.example.encuestas_api.auth.application.port.out;

import java.util.Optional;

public interface LoadCredentialPort {
    Optional<String> findPasswordHashByUserId(Long userId);
}
