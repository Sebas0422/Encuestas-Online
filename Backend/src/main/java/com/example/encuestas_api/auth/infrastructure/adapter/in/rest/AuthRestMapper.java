package com.example.encuestas_api.auth.infrastructure.adapter.in.rest;

import com.example.encuestas_api.auth.application.dto.AuthResult;
import com.example.encuestas_api.auth.infrastructure.adapter.in.rest.dto.AuthResponse;

final class AuthRestMapper {
    private AuthRestMapper(){}
    static AuthResponse toResponse(AuthResult r) {
        return new AuthResponse(r.tokenType(), r.accessToken(), r.expiresIn(),
                r.userId(), r.email(), r.fullName(), r.systemAdmin());
    }
}
