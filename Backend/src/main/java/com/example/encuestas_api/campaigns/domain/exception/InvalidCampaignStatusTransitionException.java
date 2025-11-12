package com.example.encuestas_api.campaigns.domain.exception;

import com.example.encuestas_api.campaigns.domain.model.CampaignStatus;

public class InvalidCampaignStatusTransitionException extends RuntimeException {
    public InvalidCampaignStatusTransitionException(CampaignStatus from, CampaignStatus to) {
        super("Transición de estado inválida: " + from + " → " + to);
    }
}
