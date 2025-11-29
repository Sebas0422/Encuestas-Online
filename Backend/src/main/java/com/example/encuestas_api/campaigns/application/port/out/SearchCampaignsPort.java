package com.example.encuestas_api.campaigns.application.port.out;

import com.example.encuestas_api.campaigns.application.dto.CampaignListQuery;
import com.example.encuestas_api.campaigns.domain.model.Campaign;
import com.example.encuestas_api.common.dto.PagedResult;

public interface SearchCampaignsPort {
    PagedResult<Campaign> search(CampaignListQuery query, Long userId);
}
