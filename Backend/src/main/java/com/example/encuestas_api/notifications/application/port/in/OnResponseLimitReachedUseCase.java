package com.example.encuestas_api.notifications.application.port.in;

import com.example.encuestas_api.notifications.domain.event.ResponseLimitReachedEvent;

public interface OnResponseLimitReachedUseCase {
    void handle(ResponseLimitReachedEvent event);
}
