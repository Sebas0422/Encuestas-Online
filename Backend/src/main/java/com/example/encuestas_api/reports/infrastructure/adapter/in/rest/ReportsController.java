package com.example.encuestas_api.reports.infrastructure.adapter.in.rest;

import com.example.encuestas_api.reports.application.dto.CampaignReportQuery;
import com.example.encuestas_api.reports.application.dto.FormReportQuery;
import com.example.encuestas_api.reports.application.port.in.GenerateCampaignReportUseCase;
import com.example.encuestas_api.reports.application.port.in.GenerateFormReportUseCase;
import com.example.encuestas_api.reports.infrastructure.adapter.in.rest.dto.CampaignReportResponse;
import com.example.encuestas_api.reports.infrastructure.adapter.in.rest.dto.FormReportResponse;
import com.example.encuestas_api.reports.infrastructure.adapter.in.rest.mapper.ReportsRestMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
public class ReportsController {

    private final GenerateFormReportUseCase formUC;
    private final GenerateCampaignReportUseCase campaignUC;

    public ReportsController(GenerateFormReportUseCase formUC,
                             GenerateCampaignReportUseCase campaignUC) {
        this.formUC = formUC;
        this.campaignUC = campaignUC;
    }

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
}
