package com.example.encuestas_api.users.application.port.in;

import com.example.encuestas_api.users.domain.model.User;

public interface SetSystemAdminUseCase {
    User handle(Long id, boolean systemAdmin);
}
