package com.example.encuestas_api.campaigns.application.port.in;

import com.example.encuestas_api.campaigns.application.dto.CampaignListQuery;
import com.example.encuestas_api.campaigns.domain.model.Campaign;
import com.example.encuestas_api.common.dto.PagedResult;

public interface ListCampaignsUseCase {
    PagedResult<Campaign> handle(CampaignListQuery query);
}
