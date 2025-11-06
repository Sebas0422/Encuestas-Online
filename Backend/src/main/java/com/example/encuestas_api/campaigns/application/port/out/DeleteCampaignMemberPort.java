package com.example.encuestas_api.campaigns.application.port.out;

public interface DeleteCampaignMemberPort {
    void deleteByCampaignIdAndUserId(Long campaignId, Long userId);
}
