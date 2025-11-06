package com.example.encuestas_api.users.domain.model;

import com.example.encuestas_api.users.domain.valueobject.Email;
import com.example.encuestas_api.users.domain.valueobject.FullName;

import java.time.Instant;
import java.util.Objects;

public final class User {

    private final Long id;
    private final Email email;
    private final FullName fullName;
    private final boolean systemAdmin;
    private final UserStatus status;
    private final Instant createdAt;
    private final Instant updatedAt;

    private User(Long id,
                 Email email,
                 FullName fullName,
                 boolean systemAdmin,
                 UserStatus status,
                 Instant createdAt,
                 Instant updatedAt) {

        if (email == null) throw new IllegalArgumentException("email es requerido");
        if (fullName == null) throw new IllegalArgumentException("fullName es requerido");
        if (status == null) throw new IllegalArgumentException("status es requerido");
        if (createdAt == null || updatedAt == null)
            throw new IllegalArgumentException("timestamps requeridos");

        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.systemAdmin = systemAdmin;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static User createNew(Email email, FullName fullName, boolean systemAdmin, Instant now) {
        return new User(null, email, fullName, systemAdmin, UserStatus.ACTIVE, now, now);
    }

    public static User rehydrate(Long id, Email email, FullName fullName,
                                 boolean systemAdmin, UserStatus status,
                                 Instant createdAt, Instant updatedAt) {
        return new User(id, email, fullName, systemAdmin, status, createdAt, updatedAt);
    }

    public User withId(Long id) {
        if (this.id != null && !Objects.equals(this.id, id))
            throw new IllegalStateException("id ya establecido");
        return new User(id, email, fullName, systemAdmin, status, createdAt, updatedAt);
    }

    public User rename(FullName newName, Instant now) {
        return new User(id, email, newName, systemAdmin, status, createdAt, now);
    }

    public User withStatus(UserStatus newStatus, Instant now) {
        return new User(id, email, fullName, systemAdmin, newStatus, createdAt, now);
    }

    public User withSystemAdmin(boolean newValue, Instant now) {
        return new User(id, email, fullName, newValue, status, createdAt, now);
    }

    public Long getId() { return id; }
    public Email getEmail() { return email; }
    public FullName getFullName() { return fullName; }
    public boolean isSystemAdmin() { return systemAdmin; }
    public UserStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User that)) return false;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return id == null ? System.identityHashCode(this) : id.hashCode();
    }
}
