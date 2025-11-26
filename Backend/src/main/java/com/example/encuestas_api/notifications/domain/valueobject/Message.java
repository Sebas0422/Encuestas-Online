package com.example.encuestas_api.notifications.domain.valueobject;

import java.util.Objects;

public final class Message {
    private final String subject;
    private final String body;

    public Message(String subject, String body) {
        this.subject = subject == null ? "" : subject;
        this.body = Objects.requireNonNull(body);
    }
    public String getSubject() { return subject; }
    public String getBody() { return body; }
}
