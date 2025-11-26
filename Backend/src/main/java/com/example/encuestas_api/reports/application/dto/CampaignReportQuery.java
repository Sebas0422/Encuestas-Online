package com.example.encuestas_api.reports.application.dto;

public record CampaignReportQuery(
        Long campaignId,
        boolean includeDrafts
) { }
