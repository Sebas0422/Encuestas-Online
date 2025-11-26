package com.example.encuestas_api.notifications.application.port.in;

import com.example.encuestas_api.notifications.domain.event.FormStatusChangedEvent;

public interface OnFormStatusChangedUseCase {
    void handle(FormStatusChangedEvent event);
}
