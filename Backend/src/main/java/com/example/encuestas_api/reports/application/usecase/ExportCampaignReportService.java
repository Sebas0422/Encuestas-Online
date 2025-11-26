package com.example.encuestas_api.reports.application.usecase;

import com.example.encuestas_api.reports.application.dto.*;
import com.example.encuestas_api.reports.application.port.in.ExportCampaignReportUseCase;
import com.example.encuestas_api.reports.application.port.in.GenerateCampaignReportUseCase;
import com.example.encuestas_api.reports.application.port.out.*;
import com.example.encuestas_api.reports.domain.model.CampaignReport;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ExportCampaignReportService implements ExportCampaignReportUseCase {

    private final GenerateCampaignReportUseCase generateCampaignReport;
    private final CampaignReportXlsxExporterPort xlsxExporter;
    private final CampaignReportPdfExporterPort pdfExporter;

    public ExportCampaignReportService(GenerateCampaignReportUseCase generateCampaignReport,
                                       CampaignReportXlsxExporterPort xlsxExporter,
                                       CampaignReportPdfExporterPort pdfExporter) {
        this.generateCampaignReport = generateCampaignReport;
        this.xlsxExporter = xlsxExporter;
        this.pdfExporter = pdfExporter;
    }

    @Override
    public ExportedFile handle(ExportCampaignReportQuery q) {
        CampaignReport report = generateCampaignReport.handle(
                new CampaignReportQuery(q.campaignId(), q.includeDrafts())
        );

        String filename;
        String contentType;
        byte[] bytes;

        switch (q.format()) {
            case XLSX -> {
                filename = "campaign-" + q.campaignId() + (q.includeDrafts() ? "-with-drafts" : "") + ".xlsx";
                contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
                bytes = xlsxExporter.export(report);
            }
            case PDF -> {
                filename = "campaign-" + q.campaignId() + (q.includeDrafts() ? "-with-drafts" : "") + ".pdf";
                contentType = "application/pdf";
                bytes = pdfExporter.export(report);
            }
            default -> throw new IllegalStateException("Unexpected value: " + q.format());
        }
        return new ExportedFile(filename, contentType, bytes);
    }
}
