package com.example.encuestas_api.reports.application.port.out;

import com.example.encuestas_api.reports.domain.model.CampaignReport;

public interface CampaignReportXlsxExporterPort {
    byte[] export(CampaignReport report);
}