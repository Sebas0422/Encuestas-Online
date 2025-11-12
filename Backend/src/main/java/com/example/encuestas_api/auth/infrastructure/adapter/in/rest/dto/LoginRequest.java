package com.example.encuestas_api.auth.infrastructure.adapter.in.rest.dto;

import com.example.encuestas_api.common.validation.NoWhitespace;
import com.example.encuestas_api.common.validation.ValidEmail;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank @ValidEmail @NoWhitespace String email,
        @NotBlank String password
) {}
