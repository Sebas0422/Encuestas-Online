package com.example.encuestas_api.forms.infrastructure.adapter.out.jpa.repository;

import com.example.encuestas_api.forms.infrastructure.adapter.out.jpa.entity.FormEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface FormJpaRepository extends JpaRepository<FormEntity, Long>, JpaSpecificationExecutor<FormEntity> {
    boolean existsByCampaignIdAndTitleIgnoreCase(Long campaignId, String title);
    Optional<FormEntity> findByPublicCode(String publicCode);
}
