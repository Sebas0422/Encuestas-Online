package com.example.encuestas_api.questions.infrastructure.adapter.in.rest.dto;

public record SetTextSettingsRequest(String textMode, String placeholder, Integer minLength, Integer maxLength) {}
