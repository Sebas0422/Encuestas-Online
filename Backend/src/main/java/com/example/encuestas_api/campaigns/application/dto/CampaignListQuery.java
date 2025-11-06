package com.example.encuestas_api.campaigns.application.dto;

import com.example.encuestas_api.campaigns.domain.model.CampaignStatus;

import java.time.LocalDate;

public record CampaignListQuery(
        String search,
        CampaignStatus status,
        LocalDate startFrom,
        LocalDate endTo,
        int page,
        int size
) {
    public CampaignListQuery {
        if (page < 0) throw new IllegalArgumentException("page debe ser >= 0");
        if (size <= 0 || size > 200) throw new IllegalArgumentException("size inválido");
    }
}
