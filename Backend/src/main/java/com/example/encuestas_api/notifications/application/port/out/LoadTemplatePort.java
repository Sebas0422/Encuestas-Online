// application/port/out/LoadTemplatePort.java
package com.example.encuestas_api.notifications.application.port.out;

import com.example.encuestas_api.notifications.domain.model.NotificationType;
import com.example.encuestas_api.notifications.domain.valueobject.NotificationTemplate;

import java.util.Optional;

public interface LoadTemplatePort {
    Optional<NotificationTemplate> loadByCode(String code);
    Optional<NotificationTemplate> loadByType(NotificationType type);
}
