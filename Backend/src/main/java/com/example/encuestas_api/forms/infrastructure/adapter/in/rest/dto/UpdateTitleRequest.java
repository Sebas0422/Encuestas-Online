package com.example.encuestas_api.forms.infrastructure.adapter.in.rest.dto;

import com.example.encuestas_api.common.validation.NoWhitespace;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateTitleRequest(@NotBlank @NoWhitespace @Size(max = 200) String title) {}
