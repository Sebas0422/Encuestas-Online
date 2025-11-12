package com.example.encuestas_api.auth.infrastructure.adapter.in.rest.dto;

import com.example.encuestas_api.common.validation.NoWhitespace;
import com.example.encuestas_api.common.validation.StrongPassword;
import com.example.encuestas_api.common.validation.ValidEmail;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank @ValidEmail @NoWhitespace String email,
        @NotBlank @NoWhitespace @Size(max = 255) String fullName,
        @NotBlank @StrongPassword String password,
        Boolean systemAdmin
) {}
