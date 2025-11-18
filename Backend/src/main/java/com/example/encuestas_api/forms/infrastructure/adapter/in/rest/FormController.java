package com.example.encuestas_api.forms.infrastructure.adapter.in.rest;

import com.example.encuestas_api.common.dto.PagedResult;
import com.example.encuestas_api.forms.application.dto.FormListQuery;
import com.example.encuestas_api.forms.application.port.in.*;
import com.example.encuestas_api.forms.domain.model.AccessMode;
import com.example.encuestas_api.forms.domain.model.Form;
import com.example.encuestas_api.forms.domain.model.FormStatus;
import com.example.encuestas_api.forms.infrastructure.adapter.in.rest.dto.*;
import com.example.encuestas_api.forms.infrastructure.adapter.in.rest.mapper.FormRestMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/forms")
public class FormController {

    private final CreateFormUseCase createUC;
    private final GetFormUseCase getUC;
    private final RenameFormUseCase renameUC;
    private final ChangeFormDescriptionUseCase descUC;
    private final ChangeThemeUseCase themeUC;
    private final SetAccessModeUseCase accessUC;
    private final RescheduleFormUseCase scheduleUC;
    private final SetResponseLimitPolicyUseCase limitUC;
    private final SetPresentationOptionsUseCase presentationUC;
    private final SetAnonymousModeUseCase anonymousUC;
    private final SetAllowEditBeforeSubmitUseCase allowEditUC;
    private final SetAutoSaveUseCase autoSaveUC;
    private final ChangeFormStatusUseCase statusUC;
    private final DeleteFormUseCase deleteUC;
    private final ListFormsUseCase listUC;

    public FormController(CreateFormUseCase createUC, GetFormUseCase getUC, RenameFormUseCase renameUC,
                          ChangeFormDescriptionUseCase descUC, ChangeThemeUseCase themeUC, SetAccessModeUseCase accessUC,
                          RescheduleFormUseCase scheduleUC, SetResponseLimitPolicyUseCase limitUC,
                          SetPresentationOptionsUseCase presentationUC, SetAnonymousModeUseCase anonymousUC,
                          SetAllowEditBeforeSubmitUseCase allowEditUC, SetAutoSaveUseCase autoSaveUC,
                          ChangeFormStatusUseCase statusUC, DeleteFormUseCase deleteUC, ListFormsUseCase listUC) {
        this.createUC = createUC; this.getUC = getUC; this.renameUC = renameUC; this.descUC = descUC;
        this.themeUC = themeUC; this.accessUC = accessUC; this.scheduleUC = scheduleUC; this.limitUC = limitUC;
        this.presentationUC = presentationUC; this.anonymousUC = anonymousUC; this.allowEditUC = allowEditUC;
        this.autoSaveUC = autoSaveUC; this.statusUC = statusUC; this.deleteUC = deleteUC; this.listUC = listUC;
    }

    @PostMapping
    public ResponseEntity<FormResponse> create(@Valid @RequestBody CreateFormRequest req) {
        Form f = createUC.handle(
                req.campaignId(),
                req.title(),
                req.description(),
                req.coverUrl(),
                req.themeMode(), req.themePrimary(),
                req.accessMode() == null ? null : AccessMode.valueOf(req.accessMode()),
                req.openAt(), req.closeAt(),
                req.responseLimitMode(), req.limitedN(),
                req.anonymousMode() != null && req.anonymousMode(),
                req.allowEditBeforeSubmit() != null && req.allowEditBeforeSubmit(),
                req.autoSave() != null && req.autoSave(),
                req.shuffleQuestions() != null && req.shuffleQuestions(),
                req.shuffleOptions() != null && req.shuffleOptions(),
                req.progressBar() != null && req.progressBar(),
                req.paginated() != null && req.paginated()
        );
        return ResponseEntity.ok(FormRestMapper.toResponse(f));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FormResponse> get(@PathVariable Long id) {
        var f = getUC.handle(id);
        return ResponseEntity.ok(FormRestMapper.toResponse(f));
    }

    @PatchMapping("/{id}/title")
    public ResponseEntity<FormResponse> rename(@PathVariable Long id, @Valid @RequestBody UpdateTitleRequest req) {
        var f = renameUC.handle(id, req.title());
        return ResponseEntity.ok(FormRestMapper.toResponse(f));
    }

    @PatchMapping("/{id}/description")
    public ResponseEntity<FormResponse> changeDescription(@PathVariable Long id, @Valid @RequestBody UpdateDescriptionRequest req) {
        var f = descUC.handle(id, req.description());
        return ResponseEntity.ok(FormRestMapper.toResponse(f));
    }

    @PatchMapping("/{id}/theme")
    public ResponseEntity<FormResponse> changeTheme(@PathVariable Long id, @RequestBody UpdateThemeRequest req) {
        var f = themeUC.handle(id, req.mode(), req.primaryColor());
        return ResponseEntity.ok(FormRestMapper.toResponse(f));
    }

    @PatchMapping("/{id}/access-mode")
    public ResponseEntity<FormResponse> setAccessMode(@PathVariable Long id, @RequestBody SetAccessModeRequest req) {
        var f = accessUC.handle(id, req.mode() == null ? null : AccessMode.valueOf(req.mode()));
        return ResponseEntity.ok(FormRestMapper.toResponse(f));
    }

    @PatchMapping("/{id}/schedule")
    public ResponseEntity<FormResponse> reschedule(@PathVariable Long id, @RequestBody RescheduleRequest req) {
        var f = scheduleUC.handle(id, req.openAt(), req.closeAt());
        return ResponseEntity.ok(FormRestMapper.toResponse(f));
    }

    @PatchMapping("/{id}/limit-policy")
    public ResponseEntity<FormResponse> setLimit(@PathVariable Long id, @RequestBody SetLimitPolicyRequest req) {
        var f = limitUC.handle(id, req.mode(), req.n());
        return ResponseEntity.ok(FormRestMapper.toResponse(f));
    }

    @PatchMapping("/{id}/presentation")
    public ResponseEntity<FormResponse> setPresentation(@PathVariable Long id, @RequestBody SetPresentationRequest req) {
        var f = presentationUC.handle(
                id,
                req.shuffleQuestions() != null && req.shuffleQuestions(),
                req.shuffleOptions() != null && req.shuffleOptions(),
                req.progressBar() != null && req.progressBar(),
                req.paginated() != null && req.paginated()
        );
        return ResponseEntity.ok(FormRestMapper.toResponse(f));
    }

    @PatchMapping("/{id}/anonymous")
    public ResponseEntity<FormResponse> setAnonymous(@PathVariable Long id, @RequestBody ToggleFlagRequest req) {
        var f = anonymousUC.handle(id, req.enabled() != null && req.enabled());
        return ResponseEntity.ok(FormRestMapper.toResponse(f));
    }

    @PatchMapping("/{id}/allow-edit")
    public ResponseEntity<FormResponse> setAllowEdit(@PathVariable Long id, @RequestBody ToggleFlagRequest req) {
        var f = allowEditUC.handle(id, req.enabled() != null && req.enabled());
        return ResponseEntity.ok(FormRestMapper.toResponse(f));
    }

    @PatchMapping("/{id}/autosave")
    public ResponseEntity<FormResponse> setAutoSave(@PathVariable Long id, @RequestBody ToggleFlagRequest req) {
        var f = autoSaveUC.handle(id, req.enabled() != null && req.enabled());
        return ResponseEntity.ok(FormRestMapper.toResponse(f));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<FormResponse> setStatus(@PathVariable Long id, @Valid @RequestBody ChangeStatusRequest req) {
        var f = statusUC.handle(id, req.status());
        return ResponseEntity.ok(FormRestMapper.toResponse(f));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteUC.handle(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<PagedResult<FormResponse>> list(
            @RequestParam(required = false) Long campaignId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) FormStatus status,
            @RequestParam(required = false) AccessMode accessMode,
            @RequestParam(required = false) Instant openFrom,
            @RequestParam(required = false) Instant closeTo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        var result = listUC.handle(new FormListQuery(campaignId, search, status, accessMode, openFrom, closeTo, page, size));
        var mapped = new PagedResult<>(
                result.items().stream().map(FormRestMapper::toResponse).toList(),
                result.total(), result.page(), result.size()
        );
        return ResponseEntity.ok(mapped);
    }
}
