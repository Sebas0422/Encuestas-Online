package com.example.encuestas_api.users.application.usecase;

import com.example.encuestas_api.users.application.port.in.SetSystemAdminUseCase;
import com.example.encuestas_api.users.application.port.out.UserRepositoryPort;
import com.example.encuestas_api.users.domain.exception.UserNotFoundException;
import com.example.encuestas_api.users.domain.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

@Service
@Transactional
public class SetSystemAdminService implements SetSystemAdminUseCase {

    private final UserRepositoryPort repo;
    private final Clock clock;

    public SetSystemAdminService(UserRepositoryPort repo, Clock clock) {
        this.repo = repo;
        this.clock = clock;
    }

    @Override
    public User handle(Long id, boolean systemAdmin) {
        var user = repo.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        var updated = user.withSystemAdmin(systemAdmin, Instant.now(clock));
        return repo.save(updated);
    }
}
