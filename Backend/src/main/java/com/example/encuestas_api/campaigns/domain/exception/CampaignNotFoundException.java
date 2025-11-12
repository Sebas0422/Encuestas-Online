package com.example.encuestas_api.campaigns.domain.exception;

public class CampaignNotFoundException extends RuntimeException {
    public CampaignNotFoundException(Long id) {
        super("Campaign id=" + id + " no encontrada");
    }
}
