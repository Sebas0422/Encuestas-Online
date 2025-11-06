package com.example.encuestas_api.users.infrastructure.adapter.out.jpa.repository;

import com.example.encuestas_api.users.domain.model.UserStatus;
import com.example.encuestas_api.users.infrastructure.adapter.out.jpa.entity.UserEntity;
import org.springframework.data.jpa.domain.Specification;

public final class UserSpecifications {

    private UserSpecifications() {}

    public static Specification<UserEntity> searchTerm(String term) {
        if (term == null || term.isBlank()) return null;
        String like = "%" + term.trim().toLowerCase() + "%";
        return (root, q, cb) -> cb.or(
                cb.like(cb.lower(root.get("email")), like),
                cb.like(cb.lower(root.get("fullName")), like)
        );
    }

    public static Specification<UserEntity> statusEquals(UserStatus status) {
        if (status == null) return null;
        return (root, q, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<UserEntity> systemAdminEquals(Boolean value) {
        if (value == null) return null;
        return (root, q, cb) -> cb.equal(root.get("systemAdmin"), value);
    }
}
