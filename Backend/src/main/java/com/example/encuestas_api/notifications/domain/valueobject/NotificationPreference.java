package com.example.encuestas_api.notifications.domain.valueobject;


import com.example.encuestas_api.notifications.domain.model.NotificationType;

import java.time.Instant;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

public final class NotificationPreference {
    private final Long userId;
    private final Set<NotificationType> enabledTypes;
    private final Set<Channel> channels;
    private final Instant mutedUntil;

    public NotificationPreference(Long userId,
                                  Set<NotificationType> enabledTypes,
                                  Set<Channel> channels,
                                  Instant mutedUntil) {
        this.userId = Objects.requireNonNull(userId);
        this.enabledTypes = enabledTypes == null ? EnumSet.noneOf(NotificationType.class) : EnumSet.copyOf(enabledTypes);
        this.channels = channels == null ? EnumSet.noneOf(Channel.class) : EnumSet.copyOf(channels);
        this.mutedUntil = mutedUntil;
    }

    public Long getUserId() { return userId; }
    public Set<NotificationType> getEnabledTypes() { return enabledTypes; }
    public Set<Channel> getChannels() { return channels; }
    public Instant getMutedUntil() { return mutedUntil; }

    public boolean allows(NotificationType type, Channel channel, Instant now) {
        if (mutedUntil != null && now.isBefore(mutedUntil)) return false;
        return enabledTypes.contains(type) && channels.contains(channel);
    }
}
