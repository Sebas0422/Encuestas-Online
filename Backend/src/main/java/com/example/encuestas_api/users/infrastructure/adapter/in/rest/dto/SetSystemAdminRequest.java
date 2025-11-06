package com.example.encuestas_api.users.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.NotNull;

public record SetSystemAdminRequest(@NotNull Boolean systemAdmin) { }
