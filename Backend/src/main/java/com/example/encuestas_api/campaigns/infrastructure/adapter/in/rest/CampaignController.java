package com.example.encuestas_api.campaigns.infrastructure.adapter.in.rest;

import com.example.encuestas_api.campaigns.application.dto.CampaignListQuery;
import com.example.encuestas_api.campaigns.application.port.in.*;
import com.example.encuestas_api.campaigns.domain.model.CampaignStatus;
import com.example.encuestas_api.campaigns.infrastructure.adapter.in.rest.dto.*;
import com.example.encuestas_api.campaigns.infrastructure.adapter.in.rest.mapper.CampaignRestMapper;
import com.example.encuestas_api.common.dto.PagedResult;
import com.example.encuestas_api.config.JwtAuthenticationFilter;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/campaigns")
public class CampaignController {

    private final CreateCampaignUseCase createUC;
    private final GetCampaignUseCase getUC;
    private final RenameCampaignUseCase renameUC;
    private final ChangeCampaignDescriptionUseCase descUC;
    private final RescheduleCampaignUseCase scheduleUC;
    private final ChangeCampaignStatusUseCase statusUC;
    private final DeleteCampaignUseCase deleteUC;
    private final ListCampaignsUseCase listUC;

    public CampaignController(CreateCampaignUseCase createUC,
                              GetCampaignUseCase getUC,
                              RenameCampaignUseCase renameUC,
                              ChangeCampaignDescriptionUseCase descUC,
                              RescheduleCampaignUseCase scheduleUC,
                              ChangeCampaignStatusUseCase statusUC,
                              DeleteCampaignUseCase deleteUC,
                              ListCampaignsUseCase listUC) {
        this.createUC = createUC; this.getUC = getUC; this.renameUC = renameUC;
        this.descUC = descUC; this.scheduleUC = scheduleUC; this.statusUC = statusUC;
        this.deleteUC = deleteUC; this.listUC = listUC;
    }

    @PostMapping
    public ResponseEntity<CampaignResponse> create(@Valid @RequestBody CreateCampaignRequest req, Authentication auth) {
        JwtAuthenticationFilter.AuthDetails details =
                (JwtAuthenticationFilter.AuthDetails) auth.getDetails();

        Long userId = Long.valueOf(details.uid());
        var c = createUC.handle(req.name(), req.description(), req.startDate(), req.endDate(), userId);
        return ResponseEntity.ok(CampaignRestMapper.toResponse(c));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CampaignResponse> get(@PathVariable Long id) {
        var c = getUC.handle(id);
        return ResponseEntity.ok(CampaignRestMapper.toResponse(c));
    }

    @PatchMapping("/{id}/name")
    public ResponseEntity<CampaignResponse> rename(@PathVariable Long id, @Valid @RequestBody RenameCampaignRequest req) {
        var c = renameUC.handle(id, req.name());
        return ResponseEntity.ok(CampaignRestMapper.toResponse(c));
    }

    @PatchMapping("/{id}/description")
    public ResponseEntity<CampaignResponse> changeDescription(@PathVariable Long id, @Valid @RequestBody ChangeDescriptionRequest req) {
        var c = descUC.handle(id, req.description());
        return ResponseEntity.ok(CampaignRestMapper.toResponse(c));
    }

    @PatchMapping("/{id}/schedule")
    public ResponseEntity<CampaignResponse> reschedule(@PathVariable Long id, @Valid @RequestBody RescheduleCampaignRequest req) {
        var c = scheduleUC.handle(id, req.startDate(), req.endDate());
        return ResponseEntity.ok(CampaignRestMapper.toResponse(c));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<CampaignResponse> changeStatus(@PathVariable Long id, @Valid @RequestBody ChangeStatusRequest req) {
        var c = statusUC.handle(id, req.status());
        return ResponseEntity.ok(CampaignRestMapper.toResponse(c));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteUC.handle(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<PagedResult<CampaignResponse>> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) CampaignStatus status,
            @RequestParam(required = false) LocalDate startFrom,
            @RequestParam(required = false) LocalDate endTo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication auth) {

        JwtAuthenticationFilter.AuthDetails details =
                (JwtAuthenticationFilter.AuthDetails) auth.getDetails();

        Long userId = Long.valueOf(details.uid());
        var result = listUC.handle(new CampaignListQuery(search, status, startFrom, endTo, page, size), userId);
        var mapped = new PagedResult<>(
                result.items().stream().map(CampaignRestMapper::toResponse).toList(),
                result.total(), result.page(), result.size()
        );
        return ResponseEntity.ok(mapped);
    }
}
