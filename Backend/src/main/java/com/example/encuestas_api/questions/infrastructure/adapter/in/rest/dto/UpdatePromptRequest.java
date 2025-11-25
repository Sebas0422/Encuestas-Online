package com.example.encuestas_api.questions.infrastructure.adapter.in.rest.dto;

import com.example.encuestas_api.common.validation.NoWhitespace;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdatePromptRequest(@NotBlank @NoWhitespace @Size(max=500) String prompt) {}
