package com.example.encuestas_api.campaigns.application.usecase;

import com.example.encuestas_api.campaigns.application.port.in.AddCampaignMemberUseCase;
import com.example.encuestas_api.campaigns.application.port.out.CheckUserExistsPort;
import com.example.encuestas_api.campaigns.application.port.out.LoadCampaignMemberPort;
import com.example.encuestas_api.campaigns.application.port.out.LoadCampaignPort;
import com.example.encuestas_api.campaigns.application.port.out.SaveCampaignMemberPort;
import com.example.encuestas_api.campaigns.domain.exception.CampaignMemberAlreadyExistsException;
import com.example.encuestas_api.campaigns.domain.exception.CampaignNotFoundException;
import com.example.encuestas_api.campaigns.domain.model.CampaignMember;
import com.example.encuestas_api.campaigns.domain.model.CampaignMemberRole;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

@Service
@Transactional
public class AddCampaignMemberService implements AddCampaignMemberUseCase {

    private final LoadCampaignPort loadCampaignPort;
    private final LoadCampaignMemberPort loadMemberPort;
    private final SaveCampaignMemberPort saveMemberPort;
    private final CheckUserExistsPort checkUserExists;
    private final Clock clock;

    public AddCampaignMemberService(LoadCampaignPort loadCampaignPort,
                                    LoadCampaignMemberPort loadMemberPort,
                                    SaveCampaignMemberPort saveMemberPort,
                                    CheckUserExistsPort checkUserExists,
                                    Clock clock) {
        this.loadCampaignPort = loadCampaignPort;
        this.loadMemberPort = loadMemberPort;
        this.saveMemberPort = saveMemberPort;
        this.checkUserExists = checkUserExists;
        this.clock = clock;
    }

    @Override
    public CampaignMember handle(Long campaignId, Long userId, CampaignMemberRole role) {
        loadCampaignPort.loadById(campaignId).orElseThrow(() -> new CampaignNotFoundException(campaignId));
        if (checkUserExists != null && !checkUserExists.existsUserById(userId)) {
            throw new IllegalArgumentException("Usuario " + userId + " no existe");
        }
        if (loadMemberPort.loadByCampaignIdAndUserId(campaignId, userId).isPresent()) {
            throw new CampaignMemberAlreadyExistsException(campaignId, userId);
        }
        var member = CampaignMember.createNew(campaignId, userId, role, Instant.now(clock));
        return saveMemberPort.save(member);
    }
}
