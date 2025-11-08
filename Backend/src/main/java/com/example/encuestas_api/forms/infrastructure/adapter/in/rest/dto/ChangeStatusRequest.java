package com.example.encuestas_api.forms.infrastructure.adapter.in.rest.dto;

import com.example.encuestas_api.forms.domain.model.FormStatus;
import jakarta.validation.constraints.NotNull;

public record ChangeStatusRequest(@NotNull FormStatus status) {}
