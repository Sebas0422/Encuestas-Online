package com.example.encuestas_api.notifications.application.port.in;

import com.example.encuestas_api.notifications.application.dto.EnqueueNotificationCommand;
import com.example.encuestas_api.notifications.domain.model.Notification;

public interface EnqueueNotificationUseCase {
    Notification handle(EnqueueNotificationCommand cmd);
}
