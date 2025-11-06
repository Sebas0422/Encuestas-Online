package com.example.encuestas_api.campaigns.application.port.out;

import com.example.encuestas_api.campaigns.domain.model.CampaignMember;

public interface SaveCampaignMemberPort {
    CampaignMember save(CampaignMember member);
}
