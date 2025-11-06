package com.example.encuestas_api.campaigns.infrastructure.adapter.in.rest.dto;

import com.example.encuestas_api.campaigns.domain.model.CampaignMemberRole;

import java.time.Instant;

public record CampaignMemberResponse(
        Long campaignId,
        Long userId,
        CampaignMemberRole role,
        Instant createdAt
) {}
