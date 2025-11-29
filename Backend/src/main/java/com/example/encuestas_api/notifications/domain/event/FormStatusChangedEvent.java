package com.example.encuestas_api.notifications.domain.event;

import com.example.encuestas_api.forms.domain.model.FormStatus;

import java.time.Instant;

public record FormStatusChangedEvent(Long formId,
                                     FormStatus from,
                                     FormStatus to,
                                     Instant occurredAt) {}
