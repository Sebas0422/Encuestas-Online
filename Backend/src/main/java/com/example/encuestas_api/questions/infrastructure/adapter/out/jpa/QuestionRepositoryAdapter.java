package com.example.encuestas_api.questions.infrastructure.adapter.out.jpa;

import com.example.encuestas_api.common.dto.PagedResult;
import com.example.encuestas_api.questions.application.dto.QuestionListQuery;
import com.example.encuestas_api.questions.application.port.out.*;
import com.example.encuestas_api.questions.domain.model.Question;
import com.example.encuestas_api.questions.infrastructure.adapter.out.jpa.entity.*;
import com.example.encuestas_api.questions.infrastructure.adapter.out.jpa.mapper.QuestionJpaMapper;
import com.example.encuestas_api.questions.infrastructure.adapter.out.jpa.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
public class QuestionRepositoryAdapter implements
        SaveQuestionPort, LoadQuestionPort, DeleteQuestionPort, SearchQuestionsPort,
        ComputeNextQuestionPositionPort, ReorderQuestionsPort, LoadMatchingItemsPort {

    private final QuestionJpaRepository questions;
    private final OptionJpaRepository options;
    private final MatchingLeftRepository leftRepo;
    private final MatchingRightRepository rightRepo;
    private final MatchingPairRepository pairRepo;

    public QuestionRepositoryAdapter(QuestionJpaRepository questions,
                                     OptionJpaRepository options,
                                     MatchingLeftRepository leftRepo,
                                     MatchingRightRepository rightRepo,
                                     MatchingPairRepository pairRepo) {
        this.questions = questions;
        this.options = options;
        this.leftRepo = leftRepo;
        this.rightRepo = rightRepo;
        this.pairRepo = pairRepo;
    }

    @Override
    @Transactional
    public Question save(Question q) {
        QuestionEntity e = (q.getId() == null) ? new QuestionEntity() : questions.findById(q.getId()).orElse(new QuestionEntity());
        QuestionJpaMapper.fillEntityFromDomain(q, e);
        var saved = questions.save(e);

        switch (q.getType()) {
            case CHOICE, TRUE_FALSE -> {
                options.deleteByQuestionId(saved.getId());
                var optEntities = QuestionJpaMapper.toOptionEntities(saved.getId(), q.getChoice().options());
                options.saveAll(optEntities);
            }
            case TEXT -> { /* no-op */ }
            case MATCHING -> {
                if (q.getMatching() != null) {
                    pairRepo.deleteByQuestionId(saved.getId());
                    var pairs = QuestionJpaMapper.toPairEntities(saved.getId(), q.getMatching().answerKey());
                    pairRepo.saveAll(pairs);
                }
            }
        }

        return loadById(saved.getId()).orElseThrow();
    }

    @Override
    public Optional<Question> loadById(Long id) {
        return questions.findById(id).map(e -> {
            List<OptionEntity> opt = Collections.emptyList();
            List<MatchingLeftEntity> left = Collections.emptyList();
            List<MatchingRightEntity> right = Collections.emptyList();
            List<MatchingPairEntity> pairs = Collections.emptyList();
            switch (e.getType()) {
                case CHOICE, TRUE_FALSE -> opt = options.findByQuestionIdOrderByPositionAsc(e.getId());
                case MATCHING -> {
                    left = leftRepo.findByQuestionIdOrderByPositionAsc(e.getId());
                    right = rightRepo.findByQuestionIdOrderByPositionAsc(e.getId());
                    pairs = pairRepo.findByQuestionId(e.getId());
                }
                case TEXT -> { /* no-op */ }
            }
            return QuestionJpaMapper.toDomain(e, opt, left, right, pairs);
        });
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        pairRepo.deleteByQuestionId(id);
        options.deleteByQuestionId(id);
        leftRepo.deleteByQuestionId(id);
        rightRepo.deleteByQuestionId(id);
        questions.deleteById(id);
    }

    @Override
    public PagedResult<Question> search(QuestionListQuery q) {
        Specification<QuestionEntity> spec = Specification
                .where(QuestionSpecifications.formId(q.formId()))
                .and(QuestionSpecifications.sectionId(q.sectionId()))
                .and(QuestionSpecifications.type(q.type()))
                .and(QuestionSpecifications.search(q.search()));


        Pageable pageable = PageRequest.of(q.page(), q.size(), Sort.by(Sort.Direction.ASC, "position"));
        Page<QuestionEntity> page = questions.findAll(spec, pageable);

        var items = new ArrayList<Question>();
        for (var e : page.getContent()) {
            items.add(loadById(e.getId()).orElseThrow());
        }
        return new PagedResult<>(items, page.getTotalElements(), page.getNumber(), page.getSize());
    }

    @Override
    public int nextPosition(Long formId, Long sectionId) {
        return (sectionId == null)
                ? questions.countByFormIdAndSectionIdIsNull(formId)
                : questions.countByFormIdAndSectionId(formId, sectionId);
    }

    @Override
    @Transactional
    public Question moveTo(Long questionId, Long newSectionId, int newPosition) {
        var e = questions.findById(questionId).orElseThrow();
        Long formId = e.getFormId();
        List<QuestionEntity> dest = (newSectionId == null)
                ? questions.findAll((root, q, cb) -> cb.and(
                cb.equal(root.get("formId"), formId),
                cb.isNull(root.get("sectionId"))
        ), Sort.by("position"))
                : questions.findAll((root, q, cb) -> cb.and(
                cb.equal(root.get("formId"), formId),
                cb.equal(root.get("sectionId"), newSectionId)
        ), Sort.by("position"));

        dest.removeIf(x -> Objects.equals(x.getId(), questionId));

        int idx = Math.min(Math.max(newPosition, 0), dest.size());
        e.setSectionId(newSectionId);
        dest.add(idx, e);

        // reindex
        for (int i = 0; i < dest.size(); i++) dest.get(i).setPosition(i);
        questions.saveAll(dest);

        return loadById(questionId).orElseThrow();
    }

    @Override
    public List<com.example.encuestas_api.questions.domain.model.MatchingItem> loadLeftItems(Long questionId) {
        return leftRepo.findByQuestionIdOrderByPositionAsc(questionId)
                .stream().map(le -> com.example.encuestas_api.questions.domain.model.MatchingItem
                        .rehydrate(le.getId(), le.getText()))
                .toList();
    }

    @Override
    public List<com.example.encuestas_api.questions.domain.model.MatchingItem> loadRightItems(Long questionId) {
        return rightRepo.findByQuestionIdOrderByPositionAsc(questionId)
                .stream().map(re -> com.example.encuestas_api.questions.domain.model.MatchingItem
                        .rehydrate(re.getId(), re.getText()))
                .toList();
    }
}
