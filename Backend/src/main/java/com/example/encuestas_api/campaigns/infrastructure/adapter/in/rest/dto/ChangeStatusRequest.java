package com.example.encuestas_api.campaigns.infrastructure.adapter.in.rest.dto;

import com.example.encuestas_api.campaigns.domain.model.CampaignStatus;
import jakarta.validation.constraints.NotNull;

public record ChangeStatusRequest(@NotNull CampaignStatus status) {}
