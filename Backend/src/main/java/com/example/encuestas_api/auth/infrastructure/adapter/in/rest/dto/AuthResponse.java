package com.example.encuestas_api.auth.infrastructure.adapter.in.rest.dto;

public record AuthResponse(
        String tokenType,
        String accessToken,
        long expiresIn,
        Long userId,
        String email,
        String fullName,
        boolean systemAdmin
) {}
