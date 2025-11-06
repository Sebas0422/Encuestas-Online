package com.example.encuestas_api.auth.application.port.in;

import com.example.encuestas_api.auth.application.dto.AuthResult;

public interface RegisterUseCase {
    AuthResult handle(String email, String fullName, String rawPassword, boolean systemAdmin);
}
