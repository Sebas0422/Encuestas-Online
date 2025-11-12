package com.example.encuestas_api.users.application.port.out;

import com.example.encuestas_api.common.dto.PagedResult;
import com.example.encuestas_api.users.application.dto.ListUsersQuery;
import com.example.encuestas_api.users.domain.model.User;
import com.example.encuestas_api.users.domain.valueobject.Email;

import java.util.Optional;

public interface UserRepositoryPort {
    boolean existsByEmail(Email email);
    Optional<User> findById(Long id);
    Optional<User> findByEmail(Email email);
    User save(User user);
    void deleteById(Long id);
    PagedResult<User> search(ListUsersQuery query);
}
