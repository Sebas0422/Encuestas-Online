package com.example.encuestas_api.campaigns.infrastructure.adapter.in.rest.dto;

import com.example.encuestas_api.common.validation.MinWords;
import jakarta.validation.constraints.Size;

public record ChangeDescriptionRequest(
        @Size(max = 2000) @MinWords(2) String description
) {}
