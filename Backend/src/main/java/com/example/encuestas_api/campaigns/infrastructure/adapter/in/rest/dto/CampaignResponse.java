package com.example.encuestas_api.campaigns.infrastructure.adapter.in.rest.dto;

import com.example.encuestas_api.campaigns.domain.model.CampaignStatus;

import java.time.Instant;
import java.time.LocalDate;

public record CampaignResponse(
        Long id,
        String name,
        String description,
        LocalDate startDate,
        LocalDate endDate,
        CampaignStatus status,
        Instant createdAt,
        Instant updatedAt
) {}
