package com.example.encuestas_api.notifications.domain.event;

import java.time.Instant;

public record ResponseLimitReachedEvent(Long formId,
                                        long limitN,
                                        Instant occurredAt) {}
