package com.example.encuestas_api.users.application.usecase;

import com.example.encuestas_api.users.application.port.in.RenameUserUseCase;
import com.example.encuestas_api.users.application.port.out.UserRepositoryPort;
import com.example.encuestas_api.users.domain.exception.UserNotFoundException;
import com.example.encuestas_api.users.domain.model.User;
import com.example.encuestas_api.users.domain.valueobject.FullName;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

@Service
@Transactional
public class RenameUserService implements RenameUserUseCase {

    private final UserRepositoryPort repo;
    private final Clock clock;

    public RenameUserService(UserRepositoryPort repo, Clock clock) {
        this.repo = repo;
        this.clock = clock;
    }

    @Override
    public User handle(Long id, String newFullName) {
        var user = repo.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        var updated = user.rename(FullName.of(newFullName), Instant.now(clock));
        return repo.save(updated);
    }
}
