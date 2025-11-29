package com.example.encuestas_api.notifications.infrastructure.adapter.out.jpa.mapper;

import com.example.encuestas_api.notifications.domain.model.Notification;
import com.example.encuestas_api.notifications.domain.model.NotificationType;
import com.example.encuestas_api.notifications.domain.valueobject.*;
import com.example.encuestas_api.notifications.infrastructure.adapter.out.jpa.entity.NotificationEntity;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.Map;

public final class NotificationsJpaMapper {

    private static final ObjectMapper M = new ObjectMapper();

    private NotificationsJpaMapper() {}

    public static NotificationEntity toEntity(Notification n) {
        NotificationEntity e = new NotificationEntity();
        e.setId(n.getId());
        e.setType(n.getType().name());
        e.setChannel(n.getChannel().name());
        e.setStatus(n.getStatus().name());

        if (n.getRecipient() != null) {
            e.setRecipientUserId(n.getRecipient().getUserId());
            e.setRecipientEmail(n.getRecipient().getEmail());
            e.setRecipientWebhookUrl(n.getRecipient().getWebhook());
        }
        if (n.getMessage() != null) {
            e.setSubject(n.getMessage().getSubject());
            e.setContent(n.getMessage().getBody());
        }

        try {
            e.setMetadataJson(n.getMetadata() == null ? null : M.writeValueAsString(n.getMetadata()));
        } catch (Exception ex) {
            e.setMetadataJson(null);
        }

        e.setScheduledAt(n.getScheduledAt());
        e.setSentAt(n.getSentAt());
        if (e.getCreatedAt() == null) e.setCreatedAt(Instant.now());
        e.setUpdatedAt(Instant.now());
        return e;
    }

    public static Notification toDomain(NotificationEntity e) {
        Recipient r = null;
        if (e.getRecipientUserId() != null) r = Recipient.user(e.getRecipientUserId());
        else if (e.getRecipientEmail() != null) r = Recipient.email(e.getRecipientEmail());
        else if (e.getRecipientWebhookUrl() != null) r = Recipient.webhook(e.getRecipientWebhookUrl());

        Message m = new Message(e.getSubject(), e.getContent());

        Map<String,Object> meta = Map.of();
        try {
            if (e.getMetadataJson() != null) {
                meta = M.readValue(e.getMetadataJson(), new TypeReference<>() {});
            }
        } catch (Exception ignored) {}

        Notification n = new Notification(
                NotificationType.valueOf(e.getType()),
                Channel.valueOf(e.getChannel()),
                r,
                m,
                e.getScheduledAt(),
                meta
        );
        n.setId(e.getId());
        if ("SENT".equals(e.getStatus())) n.markSent(e.getSentAt());
        if (!"SENT".equals(e.getStatus()) && !"PENDING".equals(e.getStatus())) {
        }
        return n;
    }
}
