package com.example.encuestas_api.campaigns.application.usecase;

import com.example.encuestas_api.campaigns.application.dto.MemberListQuery;
import com.example.encuestas_api.campaigns.application.port.in.ListCampaignMembersUseCase;
import com.example.encuestas_api.campaigns.application.port.out.SearchCampaignMembersPort;
import com.example.encuestas_api.campaigns.domain.model.CampaignMember;
import com.example.encuestas_api.common.dto.PagedResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ListCampaignMembersService implements ListCampaignMembersUseCase {

    private final SearchCampaignMembersPort searchPort;

    public ListCampaignMembersService(SearchCampaignMembersPort searchPort) {
        this.searchPort = searchPort;
    }

    @Override
    public PagedResult<CampaignMember> handle(MemberListQuery query) {
        return searchPort.search(query);
    }
}
