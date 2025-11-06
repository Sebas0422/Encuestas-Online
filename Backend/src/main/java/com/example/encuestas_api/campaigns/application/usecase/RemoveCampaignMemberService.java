package com.example.encuestas_api.campaigns.application.usecase;

import com.example.encuestas_api.campaigns.application.port.in.RemoveCampaignMemberUseCase;
import com.example.encuestas_api.campaigns.application.port.out.DeleteCampaignMemberPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RemoveCampaignMemberService implements RemoveCampaignMemberUseCase {

    private final DeleteCampaignMemberPort deletePort;

    public RemoveCampaignMemberService(DeleteCampaignMemberPort deletePort) {
        this.deletePort = deletePort;
    }

    @Override
    public void handle(Long campaignId, Long userId) {
        deletePort.deleteByCampaignIdAndUserId(campaignId, userId);
    }
}
