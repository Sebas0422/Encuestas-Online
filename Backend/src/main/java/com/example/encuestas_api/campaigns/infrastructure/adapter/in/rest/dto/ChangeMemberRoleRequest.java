package com.example.encuestas_api.campaigns.infrastructure.adapter.in.rest.dto;

import com.example.encuestas_api.campaigns.domain.model.CampaignMemberRole;
import jakarta.validation.constraints.NotNull;

public record ChangeMemberRoleRequest(@NotNull CampaignMemberRole role) {}
