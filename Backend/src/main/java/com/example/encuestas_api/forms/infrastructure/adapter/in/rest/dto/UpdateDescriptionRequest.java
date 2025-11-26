package com.example.encuestas_api.forms.infrastructure.adapter.in.rest.dto;

import com.example.encuestas_api.common.validation.MinWords;
import jakarta.validation.constraints.Size;

public record UpdateDescriptionRequest(@Size(max = 2000) @MinWords(2) String description) {}
