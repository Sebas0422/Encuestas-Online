package com.example.encuestas_api.campaigns.infrastructure.adapter.out.jpa.mapper;

import com.example.encuestas_api.campaigns.domain.model.CampaignMember;
import com.example.encuestas_api.campaigns.infrastructure.adapter.out.jpa.entity.CampaignEntity;
import com.example.encuestas_api.campaigns.infrastructure.adapter.out.jpa.entity.CampaignMemberEntity;

public final class CampaignMemberJpaMapper {
    private CampaignMemberJpaMapper(){}

    public static CampaignMember toDomain(CampaignMemberEntity e) {
        return CampaignMember.rehydrate(
                e.getId(),
                e.getCampaign().getId(),
                e.getUserId(),
                e.getRole(),
                e.getCreatedAt()
        );
    }

    public static CampaignMemberEntity toEntity(CampaignMember d, CampaignEntity campaignRef) {
        var e = new CampaignMemberEntity();
        e.setId(d.getId());
        e.setCampaign(campaignRef);
        e.setUserId(d.getUserId());
        e.setRole(d.getRole());
        e.setCreatedAt(d.getCreatedAt());
        return e;
    }
}
