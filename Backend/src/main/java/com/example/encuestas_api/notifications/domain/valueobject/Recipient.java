package com.example.encuestas_api.notifications.domain.valueobject;

import java.util.Objects;

public final class Recipient {
    private final RecipientType type;
    private final Long userId;
    private final String email;
    private final String webhook;

    private Recipient(RecipientType type, Long userId, String email, String webhook) {
        this.type = type;
        this.userId = userId;
        this.email = email;
        this.webhook = webhook;
    }

    public static Recipient user(Long userId) {
        return new Recipient(RecipientType.USER, Objects.requireNonNull(userId), null, null);
    }
    public static Recipient email(String email) {
        return new Recipient(RecipientType.EMAIL, null, Objects.requireNonNull(email), null);
    }
    public static Recipient webhook(String url) {
        return new Recipient(RecipientType.WEBHOOK, null, null, Objects.requireNonNull(url));
    }

    public RecipientType getType() { return type; }
    public Long getUserId() { return userId; }
    public String getEmail() { return email; }
    public String getWebhook() { return webhook; }
}
