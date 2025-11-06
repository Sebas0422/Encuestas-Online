package com.example.encuestas_api.users.application.usecase;

import com.example.encuestas_api.users.application.port.in.DeleteUserUseCase;
import com.example.encuestas_api.users.application.port.out.UserRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DeleteUserService implements DeleteUserUseCase {

    private final UserRepositoryPort repo;

    public DeleteUserService(UserRepositoryPort repo) {
        this.repo = repo;
    }

    @Override
    public void handle(Long id) {
        repo.deleteById(id);
    }
}
