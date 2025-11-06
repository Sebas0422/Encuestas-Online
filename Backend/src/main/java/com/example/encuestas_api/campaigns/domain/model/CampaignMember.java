package com.example.encuestas_api.campaigns.domain.model;

import java.time.Instant;


public final class CampaignMember {

    private final Long id;
    private final Long campaignId;
    private final Long userId;
    private final CampaignMemberRole role;
    private final Instant createdAt;

    private CampaignMember(Long id, Long campaignId, Long userId, CampaignMemberRole role, Instant createdAt) {
        if (campaignId == null) throw new IllegalArgumentException("campaignId requerido");
        if (userId == null) throw new IllegalArgumentException("userId requerido");
        if (role == null) throw new IllegalArgumentException("role requerido");
        if (createdAt == null) throw new IllegalArgumentException("createdAt requerido");
        this.id = id;
        this.campaignId = campaignId;
        this.userId = userId;
        this.role = role;
        this.createdAt = createdAt;
    }

    public static CampaignMember createNew(Long campaignId, Long userId, CampaignMemberRole role, Instant now) {
        return new CampaignMember(null, campaignId, userId, role, now);
    }

    public static CampaignMember rehydrate(Long id, Long campaignId, Long userId, CampaignMemberRole role, Instant createdAt) {
        return new CampaignMember(id, campaignId, userId, role, createdAt);
    }

    public CampaignMember withRole(CampaignMemberRole newRole) {
        return new CampaignMember(id, campaignId, userId, newRole, createdAt);
    }

    public Long getId() { return id; }
    public Long getCampaignId() { return campaignId; }
    public Long getUserId() { return userId; }
    public CampaignMemberRole getRole() { return role; }
    public Instant getCreatedAt() { return createdAt; }
}
