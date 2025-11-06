package com.example.encuestas_api.campaigns.infrastructure.adapter.out.jpa.repository;

import com.example.encuestas_api.campaigns.infrastructure.adapter.out.jpa.entity.CampaignMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface CampaignMemberJpaRepository
        extends JpaRepository<CampaignMemberEntity, Long>, JpaSpecificationExecutor<CampaignMemberEntity> {

    Optional<CampaignMemberEntity> findByCampaign_IdAndUserId(Long campaignId, Long userId);
    void deleteByCampaign_IdAndUserId(Long campaignId, Long userId);
}
