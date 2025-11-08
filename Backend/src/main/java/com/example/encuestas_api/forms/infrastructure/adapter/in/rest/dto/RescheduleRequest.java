package com.example.encuestas_api.forms.infrastructure.adapter.in.rest.dto;

import java.time.Instant;

public record RescheduleRequest(Instant openAt, Instant closeAt) {}
