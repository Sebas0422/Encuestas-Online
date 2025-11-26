package com.example.encuestas_api.reports.application.port.in;

import com.example.encuestas_api.reports.application.dto.ExportCampaignReportQuery;
import com.example.encuestas_api.reports.application.dto.ExportedFile;

public interface ExportCampaignReportUseCase {
    ExportedFile handle(ExportCampaignReportQuery query);
}
