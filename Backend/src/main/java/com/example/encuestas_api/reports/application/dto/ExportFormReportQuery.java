package com.example.encuestas_api.reports.application.dto;

public record ExportFormReportQuery(
        Long formId,
        boolean includeDrafts,
        ExportFormat format
) { }
