package com.example.encuestas_api.campaigns.application.port.out;

import com.example.encuestas_api.campaigns.domain.model.CampaignMember;

import java.util.Optional;

public interface LoadCampaignMemberPort {
    Optional<CampaignMember> loadByCampaignIdAndUserId(Long campaignId, Long userId);
}
