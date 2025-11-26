package com.example.encuestas_api.reports.application.port.out;

import com.example.encuestas_api.reports.domain.model.FormReport;

public interface FormReportXlsxExporterPort {
    byte[] export(FormReport report);
}