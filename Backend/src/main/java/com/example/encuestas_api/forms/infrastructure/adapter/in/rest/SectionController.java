package com.example.encuestas_api.forms.infrastructure.adapter.in.rest;

import com.example.encuestas_api.common.dto.PagedResult;
import com.example.encuestas_api.forms.application.dto.SectionListQuery;
import com.example.encuestas_api.forms.application.port.in.*;
import com.example.encuestas_api.forms.infrastructure.adapter.in.rest.dto.AddSectionRequest;
import com.example.encuestas_api.forms.infrastructure.adapter.in.rest.dto.SectionResponse;
import com.example.encuestas_api.forms.infrastructure.adapter.in.rest.mapper.FormRestMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/forms/{formId}/sections")
public class SectionController {

    private final AddSectionUseCase addUC;
    private final RenameSectionUseCase renameUC;
    private final MoveSectionUseCase moveUC;
    private final DeleteSectionUseCase deleteUC;
    private final ListSectionsUseCase listUC;

    public SectionController(AddSectionUseCase addUC, RenameSectionUseCase renameUC,
                             MoveSectionUseCase moveUC, DeleteSectionUseCase deleteUC,
                             ListSectionsUseCase listUC) {
        this.addUC = addUC; this.renameUC = renameUC; this.moveUC = moveUC; this.deleteUC = deleteUC; this.listUC = listUC;
    }

    @PostMapping
    public ResponseEntity<SectionResponse> add(@PathVariable Long formId, @Valid @RequestBody AddSectionRequest req) {
        var s = addUC.handle(formId, req.title());
        return ResponseEntity.ok(FormRestMapper.toResponse(s));
    }

    @PatchMapping("/{sectionId}/title")
    public ResponseEntity<SectionResponse> rename(@PathVariable Long formId,
                                                  @PathVariable Long sectionId,
                                                  @RequestBody AddSectionRequest req) {
        var s = renameUC.handle(formId, sectionId, req.title());
        return ResponseEntity.ok(FormRestMapper.toResponse(s));
    }

    @PatchMapping("/{sectionId}/move/{newPosition}")
    public ResponseEntity<SectionResponse> move(@PathVariable Long formId,
                                                @PathVariable Long sectionId,
                                                @PathVariable int newPosition) {
        var s = moveUC.handle(formId, sectionId, newPosition);
        return ResponseEntity.ok(FormRestMapper.toResponse(s));
    }

    @DeleteMapping("/{sectionId}")
    public ResponseEntity<Void> delete(@PathVariable Long formId, @PathVariable Long sectionId) {
        deleteUC.handle(formId, sectionId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<PagedResult<SectionResponse>> list(@PathVariable Long formId,
                                                             @RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "50") int size) {
        var result = listUC.handle(new SectionListQuery(formId, page, size));
        var mapped = new PagedResult<>(
                result.items().stream().map(FormRestMapper::toResponse).toList(),
                result.total(), result.page(), result.size()
        );
        return ResponseEntity.ok(mapped);
    }
}
