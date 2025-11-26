package com.example.encuestas_api.questions.infrastructure.adapter.in.rest.dto;

public record MoveQuestionRequest(Long targetSectionId, int newPosition) {}
