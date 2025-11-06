package com.example.encuestas_api.auth.application.dto;

public record AuthResult(
        String tokenType,
        String accessToken,
        long expiresIn,
        Long userId,
        String email,
        String fullName,
        boolean systemAdmin
) {}
