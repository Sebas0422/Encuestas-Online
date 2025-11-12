package com.example.encuestas_api.campaigns.application.port.in;

import com.example.encuestas_api.campaigns.domain.model.CampaignMember;
import com.example.encuestas_api.campaigns.domain.model.CampaignMemberRole;

public interface ChangeMemberRoleUseCase {
    CampaignMember handle(Long campaignId, Long userId, CampaignMemberRole newRole);
}
