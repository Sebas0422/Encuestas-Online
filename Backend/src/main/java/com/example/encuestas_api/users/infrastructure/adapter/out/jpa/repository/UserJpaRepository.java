package com.example.encuestas_api.users.infrastructure.adapter.out.jpa.repository;

import com.example.encuestas_api.users.infrastructure.adapter.out.jpa.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface UserJpaRepository
        extends JpaRepository<UserEntity, Long>, JpaSpecificationExecutor<UserEntity> {

    boolean existsByEmailIgnoreCase(String email);
    Optional<UserEntity> findByEmailIgnoreCase(String email);
}
