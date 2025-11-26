package com.example.encuestas_api.reports.infrastructure.adapter.in.rest.dto;

import java.time.Instant;
import java.util.List;

public record CampaignReportResponse(
        Long campaignId,
        int formsCount,
        long totalSubmissions,
        long submittedCount,
        long draftCount,
        double completionRate,
        Instant generatedAt,
        List<FormReportResponse> forms
) {}
