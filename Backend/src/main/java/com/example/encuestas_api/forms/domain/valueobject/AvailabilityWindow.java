package com.example.encuestas_api.forms.domain.valueobject;

import com.example.encuestas_api.forms.domain.exception.InvalidAvailabilityWindowException;

import java.time.Instant;

public final class AvailabilityWindow {
    private final Instant openAt;
    private final Instant closeAt;

    private AvailabilityWindow(Instant openAt, Instant closeAt) {
        if (openAt != null && closeAt != null && closeAt.isBefore(openAt)) {
            throw new InvalidAvailabilityWindowException("closeAt no puede ser anterior a openAt");
        }
        this.openAt = openAt; this.closeAt = closeAt;
    }
    public static AvailabilityWindow of(Instant openAt, Instant closeAt) {
        return new AvailabilityWindow(openAt, closeAt);
    }
    public Instant openAt() { return openAt; }
    public Instant closeAt() { return closeAt; }
}
