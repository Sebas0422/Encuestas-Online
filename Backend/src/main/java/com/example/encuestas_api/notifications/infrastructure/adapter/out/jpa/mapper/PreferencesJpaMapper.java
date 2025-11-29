package com.example.encuestas_api.notifications.infrastructure.adapter.out.jpa.mapper;

import com.example.encuestas_api.notifications.domain.valueobject.NotificationPreference;
import com.example.encuestas_api.notifications.infrastructure.adapter.out.jpa.entity.NotificationPreferenceEntity;

public final class PreferencesJpaMapper {
    private PreferencesJpaMapper() {}

    public static NotificationPreference toDomain(NotificationPreferenceEntity e) {
        return new NotificationPreference(
                e.getUserId(),
                e.getEnabledTypes(),
                e.getChannels(),
                e.getMutedUntil()
        );
    }
}
