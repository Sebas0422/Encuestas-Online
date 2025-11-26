package com.example.encuestas_api.questions.infrastructure.adapter.out.jpa;

import com.example.encuestas_api.questions.application.port.in.CreateMatchingQuestionUseCase.PairIdx;
import com.example.encuestas_api.questions.application.port.out.CreateMatchingQuestionPort;
import com.example.encuestas_api.questions.domain.model.*;
import com.example.encuestas_api.questions.domain.valueobject.Prompt;
import com.example.encuestas_api.questions.infrastructure.adapter.out.jpa.entity.*;
import com.example.encuestas_api.questions.infrastructure.adapter.out.jpa.repository.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Component
public class CreateMatchingQuestionAdapter implements CreateMatchingQuestionPort {

    private final QuestionJpaRepository questions;
    private final MatchingLeftRepository leftRepo;
    private final MatchingRightRepository rightRepo;
    private final MatchingPairRepository pairRepo;

    public CreateMatchingQuestionAdapter(QuestionJpaRepository questions,
                                         MatchingLeftRepository leftRepo,
                                         MatchingRightRepository rightRepo,
                                         MatchingPairRepository pairRepo) {
        this.questions = questions;
        this.leftRepo = leftRepo;
        this.rightRepo = rightRepo;
        this.pairRepo = pairRepo;
    }

    @Override
    @Transactional
    public Question create(Long formId, Long sectionId, int position, String prompt, String helpText, boolean required,
                           boolean shuffleRightColumn, List<String> leftTexts, List<String> rightTexts,
                           List<PairIdx> keyPairs) {

        var qe = new QuestionEntity();
        qe.setFormId(formId);
        qe.setSectionId(sectionId);
        qe.setPosition(position);
        qe.setType(QuestionType.MATCHING);
        qe.setPrompt(prompt);
        qe.setHelpText(helpText);
        qe.setRequired(required);
        qe.setShuffleOptions(shuffleRightColumn);
        qe.setCreatedAt(Instant.now());
        qe.setUpdatedAt(qe.getCreatedAt());
        qe = questions.save(qe);

        List<MatchingLeftEntity> left = new ArrayList<>();
        List<MatchingRightEntity> right = new ArrayList<>();
        for (int i = 0; i < leftTexts.size(); i++) {
            var le = new MatchingLeftEntity();
            le.setQuestionId(qe.getId());
            le.setText(leftTexts.get(i));
            le.setPosition(i);
            left.add(le);
        }
        for (int i = 0; i < rightTexts.size(); i++) {
            var re = new MatchingRightEntity();
            re.setQuestionId(qe.getId());
            re.setText(rightTexts.get(i));
            re.setPosition(i);
            right.add(re);
        }
        left = leftRepo.saveAll(left);
        right = rightRepo.saveAll(right);

        var lByIdx = new ArrayList<>(left);
        var rByIdx = new ArrayList<>(right);
        var pairs = new ArrayList<MatchingPairEntity>();
        for (var p : keyPairs) {
            var le = lByIdx.get(p.leftIndex());
            var re = rByIdx.get(p.rightIndex());
            var pe = new MatchingPairEntity();
            pe.setQuestionId(qe.getId());
            pe.setLeftId(le.getId());
            pe.setRightId(re.getId());
            pairs.add(pe);
        }
        pairRepo.saveAll(pairs);

        var lDomain = left.stream().map(le -> MatchingItem.rehydrate(le.getId(), le.getText())).toList();
        var rDomain = right.stream().map(re -> MatchingItem.rehydrate(re.getId(), re.getText())).toList();
        var key = new HashMap<Long, Long>();
        for (var pe : pairs) key.put(pe.getLeftId(), pe.getRightId());

        var settings = MatchingSettings.of(lDomain, rDomain, key);
        return Question.rehydrate(qe.getId(), formId, sectionId, position, QuestionType.MATCHING,
                Prompt.of(prompt), helpText, required, shuffleRightColumn,
                null, null, settings, qe.getCreatedAt(), qe.getUpdatedAt());
    }
}
