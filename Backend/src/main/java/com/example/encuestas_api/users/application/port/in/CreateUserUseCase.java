package com.example.encuestas_api.users.application.port.in;

import com.example.encuestas_api.users.domain.model.User;

public interface CreateUserUseCase {
    User handle(String email, String fullName, boolean systemAdmin);
}
