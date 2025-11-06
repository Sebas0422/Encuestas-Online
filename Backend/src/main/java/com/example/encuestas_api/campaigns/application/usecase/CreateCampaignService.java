package com.example.encuestas_api.campaigns.application.usecase;

import com.example.encuestas_api.campaigns.application.port.in.CreateCampaignUseCase;
import com.example.encuestas_api.campaigns.application.port.out.ExistsCampaignByNamePort;
import com.example.encuestas_api.campaigns.application.port.out.SaveCampaignPort;
import com.example.encuestas_api.campaigns.domain.exception.CampaignAlreadyExistsException;
import com.example.encuestas_api.campaigns.domain.model.Campaign;
import com.example.encuestas_api.campaigns.domain.valueobject.CampaignName;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;

@Service
@Transactional
public class CreateCampaignService implements CreateCampaignUseCase {

    private final ExistsCampaignByNamePort existsByName;
    private final SaveCampaignPort savePort;
    private final Clock clock;

    public CreateCampaignService(ExistsCampaignByNamePort existsByName,
                                 SaveCampaignPort savePort,
                                 Clock clock) {
        this.existsByName = existsByName;
        this.savePort = savePort;
        this.clock = clock;
    }

    @Override
    public Campaign handle(String name, String description, LocalDate startDate, LocalDate endDate) {
        String key = name == null ? "" : name.trim();
        if (existsByName.existsByNameIgnoreCase(key)) {
            throw new CampaignAlreadyExistsException(key);
        }
        Instant now = Instant.now(clock);
        Campaign c = Campaign.createNew(CampaignName.of(key), description, startDate, endDate, now);
        return savePort.save(c);
    }
}
