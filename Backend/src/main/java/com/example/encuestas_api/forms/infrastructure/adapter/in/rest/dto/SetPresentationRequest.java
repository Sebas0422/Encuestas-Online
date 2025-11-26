package com.example.encuestas_api.forms.infrastructure.adapter.in.rest.dto;

public record SetPresentationRequest(Boolean shuffleQuestions, Boolean shuffleOptions, Boolean progressBar, Boolean paginated) {}
