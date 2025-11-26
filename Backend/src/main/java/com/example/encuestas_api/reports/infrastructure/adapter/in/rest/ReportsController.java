package com.example.encuestas_api.reports.infrastructure.adapter.in.rest;

import com.example.encuestas_api.reports.application.dto.CampaignReportQuery;
import com.example.encuestas_api.reports.application.dto.FormReportQuery;
import com.example.encuestas_api.reports.application.dto.ExportedFile;
import com.example.encuestas_api.reports.application.dto.ExportFormat;
import com.example.encuestas_api.reports.application.dto.ExportFormReportQuery;
import com.example.encuestas_api.reports.application.dto.ExportCampaignReportQuery;
import com.example.encuestas_api.reports.application.port.in.GenerateCampaignReportUseCase;
import com.example.encuestas_api.reports.application.port.in.GenerateFormReportUseCase;
import com.example.encuestas_api.reports.application.port.in.ExportFormReportUseCase;
import com.example.encuestas_api.reports.application.port.in.ExportCampaignReportUseCase;
import com.example.encuestas_api.reports.infrastructure.adapter.in.rest.dto.CampaignReportResponse;
import com.example.encuestas_api.reports.infrastructure.adapter.in.rest.dto.FormReportResponse;
import com.example.encuestas_api.reports.infrastructure.adapter.in.rest.mapper.ReportsRestMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
public class ReportsController {

    private final GenerateFormReportUseCase formUC;
    private final GenerateCampaignReportUseCase campaignUC;
    private final ExportFormReportUseCase exportFormUC;
    private final ExportCampaignReportUseCase exportCampaignUC;

    public ReportsController(GenerateFormReportUseCase formUC,
                             GenerateCampaignReportUseCase campaignUC,
                             ExportFormReportUseCase exportFormUC,
                             ExportCampaignReportUseCase exportCampaignUC) {
        this.formUC = formUC;
        this.campaignUC = campaignUC;
        this.exportFormUC = exportFormUC;
        this.exportCampaignUC = exportCampaignUC;
    }

    // ---------- JSON ----------
    @GetMapping("/forms/{formId}")
    public ResponseEntity<FormReportResponse> form(@PathVariable Long formId,
                                                   @RequestParam(defaultValue = "false") boolean includeDrafts) {
        var report = formUC.handle(new FormReportQuery(formId, includeDrafts));
        return ResponseEntity.ok(ReportsRestMapper.toResponse(report));
    }

    @GetMapping("/campaigns/{campaignId}")
    public ResponseEntity<CampaignReportResponse> campaign(@PathVariable Long campaignId,
                                                           @RequestParam(defaultValue = "false") boolean includeDrafts) {
        var report = campaignUC.handle(new CampaignReportQuery(campaignId, includeDrafts));
        return ResponseEntity.ok(ReportsRestMapper.toResponse(report));
    }

    @GetMapping("/forms/{formId}/export")
    public ResponseEntity<byte[]> exportForm(@PathVariable Long formId,
                                             @RequestParam(defaultValue = "false") boolean includeDrafts,
                                             @RequestParam(defaultValue = "PDF") ExportFormat format) {
        ExportedFile file = exportFormUC.handle(new ExportFormReportQuery(formId, includeDrafts, format));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.filename() + "\"")
                .contentType(MediaType.parseMediaType(file.contentType()))
                .body(file.bytes());
    }

    @GetMapping("/campaigns/{campaignId}/export")
    public ResponseEntity<byte[]> exportCampaign(@PathVariable Long campaignId,
                                                 @RequestParam(defaultValue = "false") boolean includeDrafts,
                                                 @RequestParam(defaultValue = "PDF") ExportFormat format) {
        ExportedFile file = exportCampaignUC.handle(new ExportCampaignReportQuery(campaignId, includeDrafts, format));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.filename() + "\"")
                .contentType(MediaType.parseMediaType(file.contentType()))
                .body(file.bytes());
    }
}
