package com.example.encuestas_api.notifications.domain.model;

import com.example.encuestas_api.notifications.domain.valueobject.*;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

public class Notification {
    private Long id;
    private final NotificationType type;
    private final Channel channel;
    private final Recipient recipient;
    private Message message;
    private DeliveryStatus status;
    private int attempts;
    private final Instant scheduledAt;
    private Instant sentAt;
    private final Map<String, Object> metadata;

    public Notification(NotificationType type,
                        Channel channel,
                        Recipient recipient,
                        Message message,
                        Instant scheduledAt,
                        Map<String, Object> metadata) {
        this.type = Objects.requireNonNull(type);
        this.channel = Objects.requireNonNull(channel);
        this.recipient = Objects.requireNonNull(recipient);
        this.message = Objects.requireNonNull(message);
        this.scheduledAt = scheduledAt == null ? Instant.now() : scheduledAt;
        this.status = DeliveryStatus.PENDING;
        this.attempts = 0;
        this.metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public NotificationType getType() { return type; }
    public Channel getChannel() { return channel; }
    public Recipient getRecipient() { return recipient; }
    public Message getMessage() { return message; }
    public DeliveryStatus getStatus() { return status; }
    public int getAttempts() { return attempts; }
    public Instant getScheduledAt() { return scheduledAt; }
    public Instant getSentAt() { return sentAt; }
    public Map<String, Object> getMetadata() { return metadata; }

    public void markSent(Instant when) {
        this.status = DeliveryStatus.SENT;
        this.sentAt = when == null ? Instant.now() : when;
    }
    public void markFailed() {
        this.status = DeliveryStatus.FAILED;
        this.attempts += 1;
    }
    public void updateMessage(Message msg) { this.message = Objects.requireNonNull(msg); }
}
