package com.example.encuestas_api.campaigns.application.port.in;

import com.example.encuestas_api.campaigns.domain.model.Campaign;

public interface ChangeCampaignDescriptionUseCase {
    Campaign handle(Long id, String newDescription);
}
