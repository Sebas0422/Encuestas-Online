package com.example.encuestas_api.campaigns.application.port.out;

import com.example.encuestas_api.campaigns.application.dto.MemberListQuery;
import com.example.encuestas_api.campaigns.domain.model.CampaignMember;
import com.example.encuestas_api.common.dto.PagedResult;

public interface SearchCampaignMembersPort {
    PagedResult<CampaignMember> search(MemberListQuery query);
}
