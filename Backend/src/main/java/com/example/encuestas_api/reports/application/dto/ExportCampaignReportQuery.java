package com.example.encuestas_api.reports.application.dto;

public record ExportCampaignReportQuery(
        Long campaignId,
        boolean includeDrafts,
        ExportFormat format
) { }
