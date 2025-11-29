package com.example.encuestas_api.notifications.infrastructure.adapter.out.jpa.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "in_app_notifications")
public class InAppInboxEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(length = 300)
    private String subject;

    @Lob
    private String content;

    @Column(nullable = false)
    private Instant createdAt;

    private Instant readAt;

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getReadAt() { return readAt; }
    public void setReadAt(Instant readAt) { this.readAt = readAt; }
}
