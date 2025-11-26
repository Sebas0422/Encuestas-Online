package com.example.encuestas_api.notifications.application.port.out;

import com.example.encuestas_api.notifications.domain.valueobject.NotificationPreference;

import java.util.Map;
import java.util.Set;

public interface LoadPreferencesPort {
    Map<Long, NotificationPreference> loadByUserIds(Set<Long> userIds);
}
