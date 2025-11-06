package com.example.encuestas_api.campaigns.infrastructure.adapter.out.jpa.entity;

import com.example.encuestas_api.campaigns.domain.model.CampaignMemberRole;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "campaign_members", uniqueConstraints = {
        @UniqueConstraint(name = "uk_campaign_member_unique", columnNames = {"campaign_id", "user_id"})
})
public class CampaignMemberEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "campaign_id", nullable = false, foreignKey = @ForeignKey(name = "fk_member_campaign"))
    private CampaignEntity campaign;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CampaignMemberRole role;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = Instant.now();
    }

    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public CampaignEntity getCampaign() { return campaign; } public void setCampaign(CampaignEntity campaign) { this.campaign = campaign; }
    public Long getUserId() { return userId; } public void setUserId(Long userId) { this.userId = userId; }
    public CampaignMemberRole getRole() { return role; } public void setRole(CampaignMemberRole role) { this.role = role; }
    public Instant getCreatedAt() { return createdAt; } public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
