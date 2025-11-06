package com.example.encuestas_api.campaigns.domain.exception;

public class CampaignMemberNotFoundException extends RuntimeException {
    public CampaignMemberNotFoundException(Long campaignId, Long userId) {
        super("Miembro (campaignId=" + campaignId + ", userId=" + userId + ") no encontrado");
    }
}
