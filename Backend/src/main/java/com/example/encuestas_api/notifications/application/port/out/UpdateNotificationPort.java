package com.example.encuestas_api.notifications.application.port.out;

import com.example.encuestas_api.notifications.domain.model.Notification;

public interface UpdateNotificationPort {
    Notification update(Notification notification);
}
