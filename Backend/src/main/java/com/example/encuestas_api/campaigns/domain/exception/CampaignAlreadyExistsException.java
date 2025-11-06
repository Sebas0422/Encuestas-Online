package com.example.encuestas_api.campaigns.domain.exception;

public class CampaignAlreadyExistsException extends RuntimeException {
    public CampaignAlreadyExistsException(String name) {
        super("La campaign con nombre '" + name + "' ya existe");
    }
}
