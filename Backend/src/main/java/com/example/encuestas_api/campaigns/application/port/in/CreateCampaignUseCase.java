package com.example.encuestas_api.campaigns.application.port.in;

import com.example.encuestas_api.campaigns.domain.model.Campaign;

import java.time.LocalDate;

public interface CreateCampaignUseCase {
    Campaign handle(String name, String description, LocalDate startDate, LocalDate endDate, Long userId);

}
