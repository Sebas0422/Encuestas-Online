package com.example.encuestas_api.questions.infrastructure.adapter.in.rest;

import com.example.encuestas_api.common.dto.PagedResult;
import com.example.encuestas_api.questions.application.dto.QuestionListQuery;
import com.example.encuestas_api.questions.application.port.in.*;
import com.example.encuestas_api.questions.domain.model.QuestionType;
import com.example.encuestas_api.questions.infrastructure.adapter.in.rest.dto.*;
import com.example.encuestas_api.questions.infrastructure.adapter.in.rest.mapper.QuestionRestMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
public class QuestionController {

    private final CreateChoiceQuestionUseCase createChoice;
    private final CreateTrueFalseQuestionUseCase createTF;
    private final CreateTextQuestionUseCase createText;
    private final CreateMatchingQuestionUseCase createMatching;
    private final GetQuestionUseCase getUC;
    private final RenameQuestionUseCase renameUC;
    private final ChangeHelpUseCase helpUC;
    private final SetRequiredUseCase requiredUC;
    private final SetQuestionShuffleUseCase shuffleUC;
    private final ReplaceChoiceOptionsUseCase replaceOptionsUC;
    private final SetMultiBoundsUseCase boundsUC;
    private final SetTextSettingsUseCase textUC;
    private final SetMatchingUseCase matchingUC;
    private final MoveQuestionUseCase moveUC;
    private final DeleteQuestionUseCase deleteUC;
    private final ListQuestionsUseCase listUC;

    public QuestionController(CreateChoiceQuestionUseCase createChoice,
                              CreateTrueFalseQuestionUseCase createTF,
                              CreateTextQuestionUseCase createText,
                              CreateMatchingQuestionUseCase createMatching,
                              GetQuestionUseCase getUC,
                              RenameQuestionUseCase renameUC,
                              ChangeHelpUseCase helpUC,
                              SetRequiredUseCase requiredUC,
                              SetQuestionShuffleUseCase shuffleUC,
                              ReplaceChoiceOptionsUseCase replaceOptionsUC,
                              SetMultiBoundsUseCase boundsUC,
                              SetTextSettingsUseCase textUC,
                              SetMatchingUseCase matchingUC,
                              MoveQuestionUseCase moveUC,
                              DeleteQuestionUseCase deleteUC,
                              ListQuestionsUseCase listUC) {
        this.createChoice = createChoice; this.createTF = createTF; this.createText = createText; this.createMatching = createMatching;
        this.getUC = getUC; this.renameUC = renameUC; this.helpUC = helpUC; this.requiredUC = requiredUC;
        this.shuffleUC = shuffleUC; this.replaceOptionsUC = replaceOptionsUC; this.boundsUC = boundsUC;
        this.textUC = textUC; this.matchingUC = matchingUC; this.moveUC = moveUC; this.deleteUC = deleteUC; this.listUC = listUC;
    }

    @PostMapping("/api/forms/{formId}/questions/choice")
    public ResponseEntity<QuestionResponse> createChoice(@PathVariable Long formId,
                                                         @RequestParam(required = false) Long sectionId,
                                                         @Valid @RequestBody CreateChoiceQuestionRequest req) {
        var q = createChoice.handle(
                formId, sectionId, req.prompt(), req.helpText(), req.required(), req.shuffleOptions(),
                req.selectionMode(), req.minSelections(), req.maxSelections(),
                req.options().stream().map(o -> new CreateChoiceQuestionUseCase.OptionCmd(o.label(), o.correct())).toList()
        );
        return ResponseEntity.ok(QuestionRestMapper.toResponse(q));
    }

    @PostMapping("/api/forms/{formId}/questions/true-false")
    public ResponseEntity<QuestionResponse> createTF(@PathVariable Long formId,
                                                     @RequestParam(required = false) Long sectionId,
                                                     @Valid @RequestBody CreateTrueFalseQuestionRequest req) {
        var q = createTF.handle(formId, sectionId, req.prompt(), req.helpText(), req.required(),
                req.shuffleOptions(), req.trueIsCorrect(), req.trueLabel(), req.falseLabel());
        return ResponseEntity.ok(QuestionRestMapper.toResponse(q));
    }

    @PostMapping("/api/forms/{formId}/questions/text")
    public ResponseEntity<QuestionResponse> createText(@PathVariable Long formId,
                                                       @RequestParam(required = false) Long sectionId,
                                                       @Valid @RequestBody CreateTextQuestionRequest req) {
        var q = createText.handle(formId, sectionId, req.prompt(), req.helpText(), req.required(),
                req.textMode(), req.placeholder(), req.minLength(), req.maxLength());
        return ResponseEntity.ok(QuestionRestMapper.toResponse(q));
    }

    @PostMapping("/api/forms/{formId}/questions/matching")
    public ResponseEntity<QuestionResponse> createMatching(@PathVariable Long formId,
                                                           @RequestParam(required = false) Long sectionId,
                                                           @Valid @RequestBody CreateMatchingQuestionRequest req) {
        var keyPairs = (req.keyPairs() == null)
                ? java.util.List.<CreateMatchingQuestionUseCase.PairIdx>of()
                : req.keyPairs().stream()
                .map(p -> new CreateMatchingQuestionUseCase.PairIdx(p.leftIndex(), p.rightIndex()))
                .toList();

        var q = createMatching.handle(
                formId,
                sectionId,
                req.prompt(),
                req.helpText(),
                req.required(),
                req.shuffleRightColumn(),
                req.leftTexts(),
                req.rightTexts(),
                keyPairs
        );
        return ResponseEntity.ok(QuestionRestMapper.toResponse(q));
    }

    @GetMapping("/api/questions/{id}")
    public ResponseEntity<QuestionResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(QuestionRestMapper.toResponse(getUC.handle(id)));
    }

    @PatchMapping("/api/questions/{id}/prompt")
    public ResponseEntity<QuestionResponse> rename(@PathVariable Long id, @Valid @RequestBody UpdatePromptRequest req) {
        return ResponseEntity.ok(QuestionRestMapper.toResponse(renameUC.handle(id, req.prompt())));
    }

    @PatchMapping("/api/questions/{id}/help")
    public ResponseEntity<QuestionResponse> help(@PathVariable Long id, @Valid @RequestBody UpdateHelpRequest req) {
        return ResponseEntity.ok(QuestionRestMapper.toResponse(helpUC.handle(id, req.helpText())));
    }

    @PatchMapping("/api/questions/{id}/required")
    public ResponseEntity<QuestionResponse> required(@PathVariable Long id, @RequestBody ToggleFlagRequest req) {
        return ResponseEntity.ok(QuestionRestMapper.toResponse(requiredUC.handle(id, req.enabled() != null && req.enabled())));
    }

    @PatchMapping("/api/questions/{id}/shuffle")
    public ResponseEntity<QuestionResponse> shuffle(@PathVariable Long id, @RequestBody ToggleFlagRequest req) {
        return ResponseEntity.ok(QuestionRestMapper.toResponse(shuffleUC.handle(id, req.enabled() != null && req.enabled())));
    }

    @PutMapping("/api/questions/{id}/options")
    public ResponseEntity<QuestionResponse> replaceOptions(@PathVariable Long id, @Valid @RequestBody ReplaceOptionsRequest req) {
        var q = replaceOptionsUC.handle(id, req.options().stream()
                .map(o -> new CreateChoiceQuestionUseCase.OptionCmd(o.label(), o.correct())).toList());
        return ResponseEntity.ok(QuestionRestMapper.toResponse(q));
    }

    @PatchMapping("/api/questions/{id}/bounds")
    public ResponseEntity<QuestionResponse> setBounds(@PathVariable Long id, @RequestBody SetBoundsRequest req) {
        return ResponseEntity.ok(QuestionRestMapper.toResponse(boundsUC.handle(id, req.min(), req.max())));
    }

    @PatchMapping("/api/questions/{id}/text-settings")
    public ResponseEntity<QuestionResponse> textSettings(@PathVariable Long id, @RequestBody SetTextSettingsRequest req) {
        return ResponseEntity.ok(QuestionRestMapper.toResponse(textUC.handle(id, req.textMode(), req.placeholder(), req.minLength(), req.maxLength())));
    }

    @PatchMapping("/api/questions/{id}/matching-key")
    public ResponseEntity<QuestionResponse> matchingKey(@PathVariable Long id, @RequestBody SetMatchingKeyRequest req) {
        return ResponseEntity.ok(QuestionRestMapper.toResponse(matchingUC.handle(id, req.key())));
    }

    @PatchMapping("/api/questions/{id}/move")
    public ResponseEntity<QuestionResponse> move(@PathVariable Long id, @RequestBody MoveQuestionRequest req) {
        return ResponseEntity.ok(QuestionRestMapper.toResponse(moveUC.handle(id, req.targetSectionId(), req.newPosition())));
    }

    @DeleteMapping("/api/questions/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteUC.handle(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/forms/{formId}/questions")
    public ResponseEntity<PagedResult<QuestionResponse>> list(@PathVariable Long formId,
                                                              @RequestParam(required=false) Long sectionId,
                                                              @RequestParam(required=false) QuestionType type,
                                                              @RequestParam(required=false) String search,
                                                              @RequestParam(defaultValue="0") int page,
                                                              @RequestParam(defaultValue="50") int size) {
        var result = listUC.handle(new QuestionListQuery(formId, sectionId, type, search, page, size));
        var mapped = new PagedResult<>(
                result.items().stream().map(QuestionRestMapper::toResponse).toList(),
                result.total(), result.page(), result.size()
        );
        return ResponseEntity.ok(mapped);
    }
}
