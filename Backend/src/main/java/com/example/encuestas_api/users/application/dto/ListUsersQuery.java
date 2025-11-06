package com.example.encuestas_api.users.application.dto;

import com.example.encuestas_api.users.domain.model.UserStatus;

public record ListUsersQuery(
        String search,
        UserStatus status,
        Boolean systemAdmin,
        int page,
        int size
) {
    public ListUsersQuery {
        if (page < 0) throw new IllegalArgumentException("page debe ser >= 0");
        if (size <= 0 || size > 200) throw new IllegalArgumentException("size inválido");
    }
}
