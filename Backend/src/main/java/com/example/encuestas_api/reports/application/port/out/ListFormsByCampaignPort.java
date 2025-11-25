package com.example.encuestas_api.reports.application.port.out;

import java.util.List;

public interface ListFormsByCampaignPort {
    List<Long> findFormIdsByCampaign(Long campaignId);
}
