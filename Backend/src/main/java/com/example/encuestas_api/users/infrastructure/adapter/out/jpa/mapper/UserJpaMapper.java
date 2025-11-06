package com.example.encuestas_api.users.infrastructure.adapter.out.jpa.mapper;

import com.example.encuestas_api.users.domain.model.User;
import com.example.encuestas_api.users.domain.valueobject.Email;
import com.example.encuestas_api.users.domain.valueobject.FullName;
import com.example.encuestas_api.users.infrastructure.adapter.out.jpa.entity.UserEntity;

public final class UserJpaMapper {
    private UserJpaMapper() {}

    public static User toDomain(UserEntity e) {
        return User.rehydrate(
                e.getId(),
                Email.of(e.getEmail()),
                FullName.of(e.getFullName()),
                e.isSystemAdmin(),
                e.getStatus(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }

    public static UserEntity toEntity(User d) {
        var e = new UserEntity();
        e.setId(d.getId());
        e.setEmail(d.getEmail().getValue());
        e.setFullName(d.getFullName().getValue());
        e.setSystemAdmin(d.isSystemAdmin());
        e.setStatus(d.getStatus());
        e.setCreatedAt(d.getCreatedAt());
        e.setUpdatedAt(d.getUpdatedAt());
        return e;
    }
}
