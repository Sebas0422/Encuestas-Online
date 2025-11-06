package com.example.encuestas_api.campaigns.infrastructure.adapter.out.jpa;

import com.example.encuestas_api.campaigns.application.port.out.CheckUserExistsPort;
import com.example.encuestas_api.users.infrastructure.adapter.out.jpa.repository.UserJpaRepository;
import org.springframework.stereotype.Component;

@Component
public class CheckUserExistsAdapter implements CheckUserExistsPort {
    private final UserJpaRepository users;
    public CheckUserExistsAdapter(UserJpaRepository users) { this.users = users; }
    @Override public boolean existsUserById(Long userId) { return users.existsById(userId); }
}
