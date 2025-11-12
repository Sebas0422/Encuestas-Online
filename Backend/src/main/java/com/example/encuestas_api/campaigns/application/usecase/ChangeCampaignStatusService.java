package com.example.encuestas_api.campaigns.application.usecase;

import com.example.encuestas_api.campaigns.application.port.in.ChangeCampaignStatusUseCase;
import com.example.encuestas_api.campaigns.application.port.out.LoadCampaignPort;
import com.example.encuestas_api.campaigns.application.port.out.SaveCampaignPort;
import com.example.encuestas_api.campaigns.domain.exception.CampaignNotFoundException;
import com.example.encuestas_api.campaigns.domain.model.Campaign;
import com.example.encuestas_api.campaigns.domain.model.CampaignStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

@Service
@Transactional
public class ChangeCampaignStatusService implements ChangeCampaignStatusUseCase {

    private final LoadCampaignPort loadPort;
    private final SaveCampaignPort savePort;
    private final Clock clock;

    public ChangeCampaignStatusService(LoadCampaignPort loadPort, SaveCampaignPort savePort, Clock clock) {
        this.loadPort = loadPort;
        this.savePort = savePort;
        this.clock = clock;
    }

    @Override
    public Campaign handle(Long id, CampaignStatus targetStatus) {
        Campaign c = loadPort.loadById(id).orElseThrow(() -> new CampaignNotFoundException(id));
        Campaign updated = c.changeStatus(targetStatus, Instant.now(clock));
        return savePort.save(updated);
    }
}
