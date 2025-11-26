package com.example.encuestas_api.reports.application.port.in;

import com.example.encuestas_api.reports.application.dto.ExportFormReportQuery;
import com.example.encuestas_api.reports.application.dto.ExportedFile;

public interface ExportFormReportUseCase {
    ExportedFile handle(ExportFormReportQuery query);
}
