package com.example.encuestas_api.auth.infrastructure.adapter.out.jpa;

import com.example.encuestas_api.auth.domain.model.Credential;

public final class CredentialJpaMapper {
    private CredentialJpaMapper(){}

    public static Credential toDomain(CredentialEntity e) {
        return Credential.rehydrate(e.getId(), e.getUserId(), e.getPasswordHash(), e.getCreatedAt(), e.getUpdatedAt());
    }

    public static CredentialEntity toEntity(Credential d) {
        var e = new CredentialEntity();
        e.setId(d.getId());
        e.setUserId(d.getUserId());
        e.setPasswordHash(d.getPasswordHash());
        e.setCreatedAt(d.getCreatedAt());
        e.setUpdatedAt(d.getUpdatedAt());
        return e;
    }
}
