package com.example.encuestas_api.users.application.port.in;

import com.example.encuestas_api.users.domain.model.User;
import com.example.encuestas_api.users.domain.model.UserStatus;

public interface ChangeUserStatusUseCase {
    User handle(Long id, UserStatus newStatus);
}
