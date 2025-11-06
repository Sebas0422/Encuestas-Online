package com.example.encuestas_api.campaigns.infrastructure.adapter.out.jpa.repository;

import com.example.encuestas_api.campaigns.infrastructure.adapter.out.jpa.entity.CampaignEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CampaignJpaRepository
        extends JpaRepository<CampaignEntity, Long>, JpaSpecificationExecutor<CampaignEntity> {

    boolean existsByNameIgnoreCase(String name);
}
