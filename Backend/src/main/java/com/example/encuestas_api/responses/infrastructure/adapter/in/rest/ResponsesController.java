package com.example.encuestas_api.responses.infrastructure.adapter.in.rest;

import com.example.encuestas_api.common.dto.PagedResult;
import com.example.encuestas_api.responses.application.dto.*;
import com.example.encuestas_api.responses.application.port.in.*;
import com.example.encuestas_api.responses.domain.model.SubmissionStatus;
import com.example.encuestas_api.responses.infrastructure.adapter.in.rest.dto.*;
import com.example.encuestas_api.responses.infrastructure.adapter.in.rest.mapper.ResponsesRestMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping
public class ResponsesController {

    private final StartSubmissionUseCase startUC;
    private final SaveChoiceAnswerUseCase saveChoiceUC;
    private final SaveTrueFalseAnswerUseCase saveTFUC;
    private final SaveTextAnswerUseCase saveTextUC;
    private final SaveMatchingAnswerUseCase saveMatchUC;
    private final RemoveAnswerUseCase removeUC;
    private final SubmitSubmissionUseCase submitUC;
    private final GetSubmissionUseCase getUC;
    private final ListSubmissionsUseCase listUC;
    private final DeleteSubmissionUseCase deleteUC;

    public ResponsesController(StartSubmissionUseCase startUC,
                               SaveChoiceAnswerUseCase saveChoiceUC,
                               SaveTrueFalseAnswerUseCase saveTFUC,
                               SaveTextAnswerUseCase saveTextUC,
                               SaveMatchingAnswerUseCase saveMatchUC,
                               RemoveAnswerUseCase removeUC,
                               SubmitSubmissionUseCase submitUC,
                               GetSubmissionUseCase getUC,
                               ListSubmissionsUseCase listUC,
                               DeleteSubmissionUseCase deleteUC) {
        this.startUC = startUC; this.saveChoiceUC = saveChoiceUC; this.saveTFUC = saveTFUC;
        this.saveTextUC = saveTextUC; this.saveMatchUC = saveMatchUC; this.removeUC = removeUC;
        this.submitUC = submitUC; this.getUC = getUC; this.listUC = listUC; this.deleteUC = deleteUC;
    }

    @PostMapping("/api/forms/{formId}/submissions")
    public ResponseEntity<SubmissionResponse> start(@PathVariable Long formId,
                                                    @Valid @RequestBody StartSubmissionRequest req) {
        var cmd = ResponsesRestMapper.toCommand(formId, req);
        var s = startUC.handle(cmd);
        return ResponseEntity.ok(ResponsesRestMapper.toResponse(s));
    }

    @GetMapping("/api/forms/{formId}/submissions")
    public ResponseEntity<PagedResult<SubmissionResponse>> list(@PathVariable Long formId,
                                                                @RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "20") int size,
                                                                @RequestParam(required = false) SubmissionStatus status) {
        var result = listUC.handle(new ListSubmissionsQuery(formId, null, status, page, size));
        var mapped = new PagedResult<>(
                result.items().stream().map(ResponsesRestMapper::toResponse).toList(),
                result.total(), result.page(), result.size()
        );
        return ResponseEntity.ok(mapped);
    }

    @GetMapping("/api/submissions/{id}")
    public ResponseEntity<SubmissionResponse> get(@PathVariable Long id) {
        var s = getUC.handle(id);
        return ResponseEntity.ok(ResponsesRestMapper.toResponse(s));
    }

    @DeleteMapping("/api/submissions/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteUC.handle(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/api/submissions/{id}/answers/choice")
    public ResponseEntity<SubmissionResponse> saveChoice(@PathVariable Long id,
                                                         @Valid @RequestBody SaveChoiceAnswerRequest req) {
        var s = saveChoiceUC.handle(ResponsesRestMapper.toCommand(id, req));
        return ResponseEntity.ok(ResponsesRestMapper.toResponse(s));
    }

    @PostMapping("/api/submissions/{id}/answers/true-false")
    public ResponseEntity<SubmissionResponse> saveTF(@PathVariable Long id,
                                                     @Valid @RequestBody SaveTrueFalseAnswerRequest req) {
        var s = saveTFUC.handle(ResponsesRestMapper.toCommand(id, req));
        return ResponseEntity.ok(ResponsesRestMapper.toResponse(s));
    }

    @PostMapping("/api/submissions/{id}/answers/text")
    public ResponseEntity<SubmissionResponse> saveText(@PathVariable Long id,
                                                       @Valid @RequestBody SaveTextAnswerRequest req) {
        var s = saveTextUC.handle(ResponsesRestMapper.toCommand(id, req));
        return ResponseEntity.ok(ResponsesRestMapper.toResponse(s));
    }

    @PostMapping("/api/submissions/{id}/answers/matching")
    public ResponseEntity<SubmissionResponse> saveMatching(@PathVariable Long id,
                                                           @Valid @RequestBody SaveMatchingAnswerRequest req) {
        var s = saveMatchUC.handle(ResponsesRestMapper.toCommand(id, req));
        return ResponseEntity.ok(ResponsesRestMapper.toResponse(s));
    }

    @DeleteMapping("/api/submissions/{id}/answers/{questionId}")
    public ResponseEntity<SubmissionResponse> removeAnswer(@PathVariable Long id,
                                                           @PathVariable Long questionId) {
        var s = removeUC.handle(id, questionId);
        return ResponseEntity.ok(ResponsesRestMapper.toResponse(s));
    }

    @PostMapping("/api/submissions/{id}/submit")
    public ResponseEntity<SubmissionResponse> submit(@PathVariable Long id,
                                                     @RequestBody(required = false) SubmitSubmissionRequest ignored) {
        var s = submitUC.handle(new SubmitSubmissionCommand(id));
        return ResponseEntity.ok(ResponsesRestMapper.toResponse(s));
    }
}
