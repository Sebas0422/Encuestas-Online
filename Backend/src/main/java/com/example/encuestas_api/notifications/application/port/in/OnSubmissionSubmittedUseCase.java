package com.example.encuestas_api.notifications.application.port.in;

import com.example.encuestas_api.notifications.domain.event.SubmissionSubmittedEvent;

public interface OnSubmissionSubmittedUseCase {
    void handle(SubmissionSubmittedEvent event);
}
