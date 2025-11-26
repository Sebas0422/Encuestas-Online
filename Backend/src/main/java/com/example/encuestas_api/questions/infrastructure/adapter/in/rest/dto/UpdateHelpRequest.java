package com.example.encuestas_api.questions.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.Size;

public record UpdateHelpRequest(@Size(max=1000) String helpText) {}
