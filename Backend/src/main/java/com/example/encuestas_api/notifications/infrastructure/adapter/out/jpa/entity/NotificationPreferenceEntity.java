package com.example.encuestas_api.notifications.infrastructure.adapter.out.jpa.entity;

import com.example.encuestas_api.notifications.domain.model.NotificationType;
import com.example.encuestas_api.notifications.domain.valueobject.Channel;
import com.example.encuestas_api.notifications.infrastructure.adapter.out.jpa.converter.ChannelSetConverter;
import com.example.encuestas_api.notifications.infrastructure.adapter.out.jpa.converter.NotificationTypeSetConverter;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.Set;

@Entity
@Table(name = "notification_preferences")
public class NotificationPreferenceEntity {

    @Id
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Convert(converter = NotificationTypeSetConverter.class)
    @Column(name = "enabled_types", length = 500, nullable = false)
    private Set<NotificationType> enabledTypes;

    @Convert(converter = ChannelSetConverter.class)
    @Column(name = "channels", length = 200, nullable = false)
    private Set<Channel> channels;

    @Column(name = "muted_until")
    private Instant mutedUntil;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Set<NotificationType> getEnabledTypes() { return enabledTypes; }
    public void setEnabledTypes(Set<NotificationType> enabledTypes) { this.enabledTypes = enabledTypes; }

    public Set<Channel> getChannels() { return channels; }
    public void setChannels(Set<Channel> channels) { this.channels = channels; }

    public Instant getMutedUntil() { return mutedUntil; }
    public void setMutedUntil(Instant mutedUntil) { this.mutedUntil = mutedUntil; }
}
