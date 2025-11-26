package com.example.encuestas_api.reports.application.port.out;

import com.example.encuestas_api.reports.domain.model.FormReport;

public interface FormReportPdfExporterPort {
    byte[] export(FormReport report);
}