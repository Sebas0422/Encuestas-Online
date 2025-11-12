package com.example.encuestas_api.campaigns.application.port.out;

import com.example.encuestas_api.campaigns.domain.model.Campaign;

public interface SaveCampaignPort {
    Campaign save(Campaign campaign);
}
