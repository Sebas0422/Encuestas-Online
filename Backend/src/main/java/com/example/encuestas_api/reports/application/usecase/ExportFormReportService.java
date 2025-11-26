package com.example.encuestas_api.reports.application.usecase;

import com.example.encuestas_api.reports.application.dto.*;
import com.example.encuestas_api.reports.application.port.in.ExportFormReportUseCase;
import com.example.encuestas_api.reports.application.port.in.GenerateFormReportUseCase;
import com.example.encuestas_api.reports.application.port.out.*;
import com.example.encuestas_api.reports.domain.model.FormReport;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ExportFormReportService implements ExportFormReportUseCase {

    private final GenerateFormReportUseCase generateFormReport;
    private final FormReportXlsxExporterPort xlsxExporter;
    private final FormReportPdfExporterPort pdfExporter;

    public ExportFormReportService(GenerateFormReportUseCase generateFormReport,
                                   FormReportXlsxExporterPort xlsxExporter,
                                   FormReportPdfExporterPort pdfExporter) {
        this.generateFormReport = generateFormReport;
        this.xlsxExporter = xlsxExporter;
        this.pdfExporter = pdfExporter;
    }

    @Override
    public ExportedFile handle(ExportFormReportQuery q) {
        FormReport report = generateFormReport.handle(
                new FormReportQuery(q.formId(), q.includeDrafts())
        );

        String filename;
        String contentType;
        byte[] bytes;

        switch (q.format()) {
            case XLSX -> {
                filename = "form-" + q.formId() + (q.includeDrafts() ? "-with-drafts" : "") + ".xlsx";
                contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
                bytes = xlsxExporter.export(report);
            }
            case PDF -> {
                filename = "form-" + q.formId() + (q.includeDrafts() ? "-with-drafts" : "") + ".pdf";
                contentType = "application/pdf";
                bytes = pdfExporter.export(report);
            }
            default -> {
                throw new IllegalStateException("Unexpected value: " + q.format());
            }
        }
        return new ExportedFile(filename, contentType, bytes);
    }
}
