package com.example.encuestas_api.reports.application.port.in;

import com.example.encuestas_api.reports.application.dto.FormReportQuery;
import com.example.encuestas_api.reports.domain.model.FormReport;

public interface GenerateFormReportUseCase {
    FormReport handle(FormReportQuery query);
}
