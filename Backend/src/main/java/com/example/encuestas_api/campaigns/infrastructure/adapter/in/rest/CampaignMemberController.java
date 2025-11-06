package com.example.encuestas_api.campaigns.infrastructure.adapter.in.rest;

import com.example.encuestas_api.campaigns.application.dto.MemberListQuery;
import com.example.encuestas_api.campaigns.application.port.in.*;
import com.example.encuestas_api.campaigns.domain.model.CampaignMemberRole;
import com.example.encuestas_api.campaigns.infrastructure.adapter.in.rest.dto.*;
import com.example.encuestas_api.campaigns.infrastructure.adapter.in.rest.mapper.CampaignRestMapper;
import com.example.encuestas_api.common.dto.PagedResult;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/campaigns/{campaignId}/members")
public class CampaignMemberController {

    private final AddCampaignMemberUseCase addUC;
    private final ChangeMemberRoleUseCase changeRoleUC;
    private final RemoveCampaignMemberUseCase removeUC;
    private final ListCampaignMembersUseCase listUC;

    public CampaignMemberController(AddCampaignMemberUseCase addUC,
                                    ChangeMemberRoleUseCase changeRoleUC,
                                    RemoveCampaignMemberUseCase removeUC,
                                    ListCampaignMembersUseCase listUC) {
        this.addUC = addUC; this.changeRoleUC = changeRoleUC; this.removeUC = removeUC; this.listUC = listUC;
    }

    @PostMapping
    public ResponseEntity<CampaignMemberResponse> add(@PathVariable Long campaignId,
                                                      @Valid @RequestBody AddMemberRequest req) {
        var m = addUC.handle(campaignId, req.userId(), req.role());
        return ResponseEntity.ok(CampaignRestMapper.toResponse(m));
    }

    @PatchMapping("/{userId}/role")
    public ResponseEntity<CampaignMemberResponse> changeRole(@PathVariable Long campaignId,
                                                             @PathVariable Long userId,
                                                             @Valid @RequestBody ChangeMemberRoleRequest req) {
        var m = changeRoleUC.handle(campaignId, userId, req.role());
        return ResponseEntity.ok(CampaignRestMapper.toResponse(m));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> remove(@PathVariable Long campaignId, @PathVariable Long userId) {
        removeUC.handle(campaignId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<PagedResult<CampaignMemberResponse>> list(@PathVariable Long campaignId,
                                                                    @RequestParam(required = false) CampaignMemberRole role,
                                                                    @RequestParam(defaultValue = "0") int page,
                                                                    @RequestParam(defaultValue = "20") int size) {

        var result = listUC.handle(new MemberListQuery(campaignId, role, page, size));
        var mapped = new PagedResult<>(
                result.items().stream().map(CampaignRestMapper::toResponse).toList(),
                result.total(), result.page(), result.size()
        );
        return ResponseEntity.ok(mapped);
    }
}
