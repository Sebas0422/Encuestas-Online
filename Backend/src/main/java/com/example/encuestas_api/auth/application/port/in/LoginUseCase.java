package com.example.encuestas_api.auth.application.port.in;

import com.example.encuestas_api.auth.application.dto.AuthResult;

public interface LoginUseCase {
    AuthResult handle(String email, String rawPassword);
}
