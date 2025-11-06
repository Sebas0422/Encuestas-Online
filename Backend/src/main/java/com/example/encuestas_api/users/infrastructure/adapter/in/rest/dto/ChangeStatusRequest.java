package com.example.encuestas_api.users.infrastructure.adapter.in.rest.dto;

import com.example.encuestas_api.users.domain.model.UserStatus;
import jakarta.validation.constraints.NotNull;

public record ChangeStatusRequest(@NotNull UserStatus status) { }
