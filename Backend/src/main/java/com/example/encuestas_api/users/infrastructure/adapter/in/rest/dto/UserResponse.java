package com.example.encuestas_api.users.infrastructure.adapter.in.rest.dto;

import com.example.encuestas_api.users.domain.model.UserStatus;

import java.time.Instant;

public record UserResponse(
        Long id,
        String email,
        String fullName,
        boolean systemAdmin,
        UserStatus status,
        Instant createdAt,
        Instant updatedAt
) {}
