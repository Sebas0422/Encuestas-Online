package com.example.encuestas_api.campaigns.application.usecase;

import com.example.encuestas_api.campaigns.application.port.in.RescheduleCampaignUseCase;
import com.example.encuestas_api.campaigns.application.port.out.LoadCampaignPort;
import com.example.encuestas_api.campaigns.application.port.out.SaveCampaignPort;
import com.example.encuestas_api.campaigns.domain.exception.CampaignNotFoundException;
import com.example.encuestas_api.campaigns.domain.model.Campaign;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;

@Service
@Transactional
public class RescheduleCampaignService implements RescheduleCampaignUseCase {

    private final LoadCampaignPort loadPort;
    private final SaveCampaignPort savePort;
    private final Clock clock;

    public RescheduleCampaignService(LoadCampaignPort loadPort, SaveCampaignPort savePort, Clock clock) {
        this.loadPort = loadPort;
        this.savePort = savePort;
        this.clock = clock;
    }

    @Override
    public Campaign handle(Long id, LocalDate newStart, LocalDate newEnd) {
        Campaign c = loadPort.loadById(id).orElseThrow(() -> new CampaignNotFoundException(id));
        Campaign updated = c.reschedule(newStart, newEnd, Instant.now(clock));
        return savePort.save(updated);
    }
}
