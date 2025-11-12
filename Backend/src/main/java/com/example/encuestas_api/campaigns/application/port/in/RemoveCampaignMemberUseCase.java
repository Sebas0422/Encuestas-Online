package com.example.encuestas_api.campaigns.application.port.in;

public interface RemoveCampaignMemberUseCase {
    void handle(Long campaignId, Long userId);
}
