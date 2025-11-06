package com.example.encuestas_api.users.application.usecase;

import com.example.encuestas_api.users.application.port.in.ChangeUserStatusUseCase;
import com.example.encuestas_api.users.application.port.out.UserRepositoryPort;
import com.example.encuestas_api.users.domain.exception.UserNotFoundException;
import com.example.encuestas_api.users.domain.model.User;
import com.example.encuestas_api.users.domain.model.UserStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

@Service
@Transactional
public class ChangeUserStatusService implements ChangeUserStatusUseCase {

    private final UserRepositoryPort repo;
    private final Clock clock;

    public ChangeUserStatusService(UserRepositoryPort repo, Clock clock) {
        this.repo = repo;
        this.clock = clock;
    }

    @Override
    public User handle(Long id, UserStatus newStatus) {
        var user = repo.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        var updated = user.withStatus(newStatus, Instant.now(clock));
        return repo.save(updated);
    }
}
