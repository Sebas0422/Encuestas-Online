package com.example.encuestas_api.campaigns.application.port.in;

import com.example.encuestas_api.campaigns.domain.model.Campaign;
import com.example.encuestas_api.campaigns.domain.model.CampaignStatus;

public interface ChangeCampaignStatusUseCase {
    Campaign handle(Long id, CampaignStatus targetStatus);
}
