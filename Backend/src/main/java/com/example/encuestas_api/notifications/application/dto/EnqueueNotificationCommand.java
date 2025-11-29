package com.example.encuestas_api.notifications.application.dto;

import com.example.encuestas_api.notifications.domain.model.NotificationType;
import com.example.encuestas_api.notifications.domain.valueobject.Channel;
import com.example.encuestas_api.notifications.domain.valueobject.Recipient;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

public record EnqueueNotificationCommand(
        NotificationType type,
        Map<String, Object> model,
        Set<Channel> defaultChannels,
        Iterable<Recipient> recipients,
        Instant scheduledAt,
        Map<String, Object> metadata
) { }
