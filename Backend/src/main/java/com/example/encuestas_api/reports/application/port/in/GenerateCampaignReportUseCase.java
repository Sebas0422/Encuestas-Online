package com.example.encuestas_api.reports.application.port.in;

import com.example.encuestas_api.reports.application.dto.CampaignReportQuery;
import com.example.encuestas_api.reports.domain.model.CampaignReport;

public interface GenerateCampaignReportUseCase {
    CampaignReport handle(CampaignReportQuery query);
}
