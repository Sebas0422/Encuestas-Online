package com.example.encuestas_api.campaigns.application.usecase;

import com.example.encuestas_api.campaigns.application.port.in.RenameCampaignUseCase;
import com.example.encuestas_api.campaigns.application.port.out.LoadCampaignPort;
import com.example.encuestas_api.campaigns.application.port.out.SaveCampaignPort;
import com.example.encuestas_api.campaigns.domain.exception.CampaignNotFoundException;
import com.example.encuestas_api.campaigns.domain.model.Campaign;
import com.example.encuestas_api.campaigns.domain.valueobject.CampaignName;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

@Service
@Transactional
public class RenameCampaignService implements RenameCampaignUseCase {

    private final LoadCampaignPort loadPort;
    private final SaveCampaignPort savePort;
    private final Clock clock;

    public RenameCampaignService(LoadCampaignPort loadPort, SaveCampaignPort savePort, Clock clock) {
        this.loadPort = loadPort;
        this.savePort = savePort;
        this.clock = clock;
    }

    @Override
    public Campaign handle(Long id, String newName) {
        Campaign c = loadPort.loadById(id).orElseThrow(() -> new CampaignNotFoundException(id));
        Campaign updated = c.rename(CampaignName.of(newName), Instant.now(clock));
        return savePort.save(updated);
    }
}
