package com.example.encuestas_api.auth.infrastructure.adapter.out.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CredentialJpaRepository extends JpaRepository<CredentialEntity, Long> {
    Optional<CredentialEntity> findByUserId(Long userId);
}
