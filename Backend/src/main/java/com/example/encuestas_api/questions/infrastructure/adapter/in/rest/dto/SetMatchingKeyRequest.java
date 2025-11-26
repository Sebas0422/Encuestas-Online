package com.example.encuestas_api.questions.infrastructure.adapter.in.rest.dto;

import java.util.Map;

public record SetMatchingKeyRequest(Map<Long, Long> key) {}
