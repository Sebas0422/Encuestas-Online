package com.example.encuestas_api.campaigns.infrastructure.adapter.out.jpa.mapper;

import com.example.encuestas_api.campaigns.domain.model.Campaign;
import com.example.encuestas_api.campaigns.domain.valueobject.CampaignName;
import com.example.encuestas_api.campaigns.infrastructure.adapter.out.jpa.entity.CampaignEntity;

public final class CampaignJpaMapper {
    private CampaignJpaMapper(){}

    public static Campaign toDomain(CampaignEntity e) {
        return Campaign.rehydrate(
                e.getId(),
                CampaignName.of(e.getName()),
                e.getDescription(),
                e.getStartDate(),
                e.getEndDate(),
                e.getStatus(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }

    public static CampaignEntity toEntity(Campaign d) {
        var e = new CampaignEntity();
        e.setId(d.getId());
        e.setName(d.getName().getValue());
        e.setDescription(d.getDescription());
        e.setStartDate(d.getStartDate());
        e.setEndDate(d.getEndDate());
        e.setStatus(d.getStatus());
        e.setCreatedAt(d.getCreatedAt());
        e.setUpdatedAt(d.getUpdatedAt());
        return e;
    }
}
