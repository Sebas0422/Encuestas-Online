package com.example.encuestas_api.users.infrastructure.adapter.out.jpa.entity;

import com.example.encuestas_api.users.domain.model.UserStatus;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(name = "uk_users_email", columnNames = "email")
})
public class UserEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255, unique = true)
    private String email; // siempre en minúsculas

    @Column(name = "full_name", nullable = false, length = 255)
    private String fullName;

    @Column(name = "is_system_admin", nullable = false)
    private boolean systemAdmin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    void prePersist() {
        this.email = this.email == null ? null : this.email.toLowerCase();
        this.createdAt = this.createdAt == null ? Instant.now() : this.createdAt;
        this.updatedAt = this.updatedAt == null ? this.createdAt : this.updatedAt;
        if (this.status == null) this.status = UserStatus.ACTIVE;
    }

    @PreUpdate
    void preUpdate() {
        this.email = this.email == null ? null : this.email.toLowerCase();
        this.updatedAt = Instant.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public boolean isSystemAdmin() { return systemAdmin; }
    public void setSystemAdmin(boolean systemAdmin) { this.systemAdmin = systemAdmin; }
    public UserStatus getStatus() { return status; }
    public void setStatus(UserStatus status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
