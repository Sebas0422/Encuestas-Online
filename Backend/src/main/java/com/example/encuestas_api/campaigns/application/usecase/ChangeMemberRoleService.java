package com.example.encuestas_api.campaigns.application.usecase;

import com.example.encuestas_api.campaigns.application.port.in.ChangeMemberRoleUseCase;
import com.example.encuestas_api.campaigns.application.port.out.LoadCampaignMemberPort;
import com.example.encuestas_api.campaigns.application.port.out.SaveCampaignMemberPort;
import com.example.encuestas_api.campaigns.domain.exception.CampaignMemberNotFoundException;
import com.example.encuestas_api.campaigns.domain.model.CampaignMember;
import com.example.encuestas_api.campaigns.domain.model.CampaignMemberRole;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ChangeMemberRoleService implements ChangeMemberRoleUseCase {

    private final LoadCampaignMemberPort loadMemberPort;
    private final SaveCampaignMemberPort saveMemberPort;

    public ChangeMemberRoleService(LoadCampaignMemberPort loadMemberPort,
                                   SaveCampaignMemberPort saveMemberPort) {
        this.loadMemberPort = loadMemberPort;
        this.saveMemberPort = saveMemberPort;
    }

    @Override
    public CampaignMember handle(Long campaignId, Long userId, CampaignMemberRole newRole) {
        var member = loadMemberPort.loadByCampaignIdAndUserId(campaignId, userId)
                .orElseThrow(() -> new CampaignMemberNotFoundException(campaignId, userId));
        var updated = member.withRole(newRole);
        return saveMemberPort.save(updated);
    }
}
