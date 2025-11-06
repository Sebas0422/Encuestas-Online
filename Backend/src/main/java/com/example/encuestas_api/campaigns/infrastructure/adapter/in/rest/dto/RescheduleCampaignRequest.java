package com.example.encuestas_api.campaigns.infrastructure.adapter.in.rest.dto;

import java.time.LocalDate;

public record RescheduleCampaignRequest(
        LocalDate startDate,
        LocalDate endDate
) {}
