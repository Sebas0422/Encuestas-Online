package com.example.encuestas_api.reports.application.dto;

public record FormReportQuery(
        Long formId,
        boolean includeDrafts
) { }
