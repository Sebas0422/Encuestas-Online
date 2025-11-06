package com.example.encuestas_api.campaigns.application.port.out;

import com.example.encuestas_api.campaigns.domain.model.Campaign;

import java.util.Optional;

public interface LoadCampaignPort {
    Optional<Campaign> loadById(Long id);
}
