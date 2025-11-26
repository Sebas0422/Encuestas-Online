package com.example.encuestas_api.questions.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.Size;

import java.util.List;

public record ReplaceOptionsRequest(@Size(min=2) List<OptionDTO> options) {
    public record OptionDTO(String label, boolean correct) {}
}
