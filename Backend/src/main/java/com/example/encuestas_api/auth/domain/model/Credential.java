package com.example.encuestas_api.auth.domain.model;

import java.time.Instant;

public final class Credential {
    private final Long id;
    private final Long userId;
    private final String passwordHash;
    private final Instant createdAt;
    private final Instant updatedAt;

    private Credential(Long id, Long userId, String passwordHash, Instant createdAt, Instant updatedAt) {
        if (userId == null) throw new IllegalArgumentException("userId requerido");
        if (passwordHash == null || passwordHash.isBlank()) throw new IllegalArgumentException("passwordHash requerido");
        this.id = id;
        this.userId = userId;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Credential createNew(Long userId, String passwordHash, Instant now) {
        return new Credential(null, userId, passwordHash, now, now);
    }

    public static Credential rehydrate(Long id, Long userId, String passwordHash, Instant createdAt, Instant updatedAt) {
        return new Credential(id, userId, passwordHash, createdAt, updatedAt);
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getPasswordHash() { return passwordHash; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
