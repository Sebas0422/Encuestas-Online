package com.example.encuestas_api.questions.application.usecase;

import com.example.encuestas_api.questions.application.port.in.CreateChoiceQuestionUseCase;
import com.example.encuestas_api.questions.application.port.out.CheckFormExistsPort;
import com.example.encuestas_api.questions.application.port.out.CheckSectionBelongsToFormPort;
import com.example.encuestas_api.questions.application.port.out.ComputeNextQuestionPositionPort;
import com.example.encuestas_api.questions.application.port.out.SaveQuestionPort;
import com.example.encuestas_api.questions.domain.model.*;
import com.example.encuestas_api.questions.domain.valueobject.Prompt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;

@Service
@Transactional
public class CreateChoiceQuestionService implements CreateChoiceQuestionUseCase {

    private final CheckFormExistsPort formExists;
    private final CheckSectionBelongsToFormPort sectionBelongs;
    private final ComputeNextQuestionPositionPort nextPos;
    private final SaveQuestionPort savePort;
    private final Clock clock;

    public CreateChoiceQuestionService(CheckFormExistsPort formExists,
                                       CheckSectionBelongsToFormPort sectionBelongs,
                                       ComputeNextQuestionPositionPort nextPos,
                                       SaveQuestionPort savePort,
                                       Clock clock) {
        this.formExists = formExists;
        this.sectionBelongs = sectionBelongs;
        this.nextPos = nextPos;
        this.savePort = savePort;
        this.clock = clock;
    }

    @Override
    public Question handle(Long formId, Long sectionId, String prompt, String helpText, boolean required,
                           boolean shuffleOptions, String selectionMode, Integer minSelections, Integer maxSelections,
                           java.util.List<OptionCmd> optionsCmd) {

        if (formId == null || !formExists.exists(formId)) throw new IllegalArgumentException("formId inválido");
        if (!sectionBelongs.belongs(formId, sectionId)) throw new IllegalArgumentException("sectionId no pertenece al form");

        int position = nextPos.nextPosition(formId, sectionId);

        var opts = new ArrayList<Option>();
        for (var oc : optionsCmd) {
            opts.add(Option.newOf(oc.label(), oc.correct()));
        }

        ChoiceSettings settings = switch (selectionMode == null ? "SINGLE" : selectionMode) {
            case "MULTI" -> ChoiceSettings.multi(opts, minSelections, maxSelections);
            default      -> ChoiceSettings.single(opts);
        };

        var now = Instant.now(clock);
        var q = Question.newChoice(formId, sectionId, position, Prompt.of(prompt), helpText, required, shuffleOptions, settings, now);
        return savePort.save(q);
    }
}
