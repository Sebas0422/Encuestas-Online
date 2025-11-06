package com.example.encuestas_api.users.infrastructure.adapter.out.jpa;

import com.example.encuestas_api.common.dto.PagedResult;
import com.example.encuestas_api.users.application.dto.ListUsersQuery;
import com.example.encuestas_api.users.application.port.out.UserRepositoryPort;
import com.example.encuestas_api.users.domain.model.User;
import com.example.encuestas_api.users.domain.valueobject.Email;
import com.example.encuestas_api.users.infrastructure.adapter.out.jpa.entity.UserEntity;
import com.example.encuestas_api.users.infrastructure.adapter.out.jpa.mapper.UserJpaMapper;
import com.example.encuestas_api.users.infrastructure.adapter.out.jpa.repository.UserJpaRepository;
import com.example.encuestas_api.users.infrastructure.adapter.out.jpa.repository.UserSpecifications;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserJpaRepository jpa;

    public UserRepositoryAdapter(UserJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public boolean existsByEmail(Email email) {
        return jpa.existsByEmailIgnoreCase(email.getValue());
    }

    @Override
    public Optional<User> findById(Long id) {
        return jpa.findById(id).map(UserJpaMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return jpa.findByEmailIgnoreCase(email.getValue()).map(UserJpaMapper::toDomain);
    }

    @Override
    public User save(User user) {
        UserEntity saved = jpa.save(UserJpaMapper.toEntity(user));
        return UserJpaMapper.toDomain(saved);
    }

    @Override
    public void deleteById(Long id) {
        jpa.deleteById(id);
    }

    @Override
    public PagedResult<User> search(ListUsersQuery query) {
        Specification<UserEntity> spec = Specification
                .where(UserSpecifications.searchTerm(query.search()))
                .and(UserSpecifications.statusEquals(query.status()))
                .and(UserSpecifications.systemAdminEquals(query.systemAdmin()));

        Pageable pageable = PageRequest.of(query.page(), query.size(), Sort.by(Sort.Direction.DESC, "id"));
        Page<UserEntity> page = jpa.findAll(spec, pageable);

        var items = page.getContent().stream().map(UserJpaMapper::toDomain).toList();
        return new PagedResult<>(items, page.getTotalElements(), page.getNumber(), page.getSize());
    }
}
