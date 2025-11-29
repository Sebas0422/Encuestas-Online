package com.example.encuestas_api.notifications.application.port.out;

import com.example.encuestas_api.notifications.domain.model.Notification;
import com.example.encuestas_api.notifications.domain.valueobject.DeliveryStatus;

import java.time.Instant;
import java.util.List;

public interface FindPendingNotificationsPort {
    List<Notification> findPending(DeliveryStatus status, Instant cutoff, int limit);
}
