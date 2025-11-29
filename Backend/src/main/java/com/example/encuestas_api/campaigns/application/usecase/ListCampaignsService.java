package com.example.encuestas_api.campaigns.application.usecase;

import com.example.encuestas_api.campaigns.application.dto.CampaignListQuery;
import com.example.encuestas_api.campaigns.application.port.in.ListCampaignsUseCase;
import com.example.encuestas_api.campaigns.application.port.out.SearchCampaignsPort;
import com.example.encuestas_api.campaigns.domain.model.Campaign;
import com.example.encuestas_api.common.dto.PagedResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ListCampaignsService implements ListCampaignsUseCase {

    private final SearchCampaignsPort searchPort;

    public ListCampaignsService(SearchCampaignsPort searchPort) { this.searchPort = searchPort; }

    @Override
    public PagedResult<Campaign> handle(CampaignListQuery query, Long userId) {
        return searchPort.search(query, userId);
    }
}
