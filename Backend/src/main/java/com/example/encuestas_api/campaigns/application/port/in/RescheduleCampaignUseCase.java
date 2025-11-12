package com.example.encuestas_api.campaigns.application.port.in;

import com.example.encuestas_api.campaigns.domain.model.Campaign;

import java.time.LocalDate;

public interface RescheduleCampaignUseCase {
    Campaign handle(Long id, LocalDate newStart, LocalDate newEnd);
}
