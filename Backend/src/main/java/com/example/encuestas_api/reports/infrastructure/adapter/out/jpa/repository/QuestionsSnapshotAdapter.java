package com.example.encuestas_api.reports.infrastructure.adapter.out.jpa.repository;

import com.example.encuestas_api.questions.domain.model.TextMode;
import com.example.encuestas_api.reports.application.port.out.QuestionsSnapshotPort;
import com.example.encuestas_api.responses.domain.valueobject.QuestionKind;
import com.example.encuestas_api.responses.domain.valueobject.QuestionSnapshot;
import com.example.encuestas_api.responses.domain.valueobject.SelectionMode;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Transactional(readOnly = true)
public class QuestionsSnapshotAdapter implements QuestionsSnapshotPort {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Map<Long, QuestionSnapshot> byFormId(Long formId) {
        var questions = em.createQuery("""
                select q
                from com.example.encuestas_api.questions.infrastructure.adapter.out.jpa.entity.QuestionEntity q
                where q.formId = :fid
                order by q.position asc
                """, com.example.encuestas_api.questions.infrastructure.adapter.out.jpa.entity.QuestionEntity.class)
                .setParameter("fid", formId)
                .getResultList();

        Map<Long, QuestionSnapshot> out = new LinkedHashMap<>();

        for (var q : questions) {
            var builder = new QuestionSnapshot.Builder()
                    .questionId(q.getId())
                    .required(q.isRequired())
                    .kind(mapKind(q.getType()));

            // CHOICE
            if (q.getType() == com.example.encuestas_api.questions.domain.model.QuestionType.CHOICE) {
                var options = em.createQuery("""
                        select o
                        from com.example.encuestas_api.questions.infrastructure.adapter.out.jpa.entity.OptionEntity o
                        where o.questionId = :qid
                        order by o.position asc
                        """, com.example.encuestas_api.questions.infrastructure.adapter.out.jpa.entity.OptionEntity.class)
                        .setParameter("qid", q.getId())
                        .getResultList();

                var optionIds = options.stream()
                        .map(com.example.encuestas_api.questions.infrastructure.adapter.out.jpa.entity.OptionEntity::getId)
                        .collect(Collectors.toList());

                // q.getSelectionMode() es String en la entity
                SelectionMode sel =
                        "MULTI".equalsIgnoreCase(q.getSelectionMode()) ? SelectionMode.MULTI : SelectionMode.SINGLE;

                builder.choice(
                        sel,
                        q.getMinSelections(),
                        q.getMaxSelections(),
                        optionIds
                );
            }

            // TEXT
            if (q.getType() == com.example.encuestas_api.questions.domain.model.QuestionType.TEXT) {
                // q.getTextMode() es String en la entity
                TextMode mode =
                        "LONG".equalsIgnoreCase(q.getTextMode()) ? TextMode.LONG : TextMode.SHORT;

                builder.text(
                        mode,
                        q.getMinLength(),
                        q.getMaxLength()
                );
            }

            // MATCHING
            if (q.getType() == com.example.encuestas_api.questions.domain.model.QuestionType.MATCHING) {
                var left = em.createQuery("""
                        select m
                        from com.example.encuestas_api.questions.infrastructure.adapter.out.jpa.entity.MatchingLeftEntity m
                        where m.questionId = :qid
                        order by m.id asc
                        """, com.example.encuestas_api.questions.infrastructure.adapter.out.jpa.entity.MatchingLeftEntity.class)
                        .setParameter("qid", q.getId())
                        .getResultList();

                var right = em.createQuery("""
                        select m
                        from com.example.encuestas_api.questions.infrastructure.adapter.out.jpa.entity.MatchingRightEntity m
                        where m.questionId = :qid
                        order by m.id asc
                        """, com.example.encuestas_api.questions.infrastructure.adapter.out.jpa.entity.MatchingRightEntity.class)
                        .setParameter("qid", q.getId())
                        .getResultList();

                builder.matching(
                        left.stream().map(com.example.encuestas_api.questions.infrastructure.adapter.out.jpa.entity.MatchingLeftEntity::getId).toList(),
                        right.stream().map(com.example.encuestas_api.questions.infrastructure.adapter.out.jpa.entity.MatchingRightEntity::getId).toList()
                );
            }

            out.put(q.getId(), builder.build());
        }

        return out;
    }

    private QuestionKind mapKind(com.example.encuestas_api.questions.domain.model.QuestionType t) {
        return switch (t) {
            case CHOICE     -> QuestionKind.CHOICE;
            case TRUE_FALSE -> QuestionKind.TRUE_FALSE;
            case TEXT       -> QuestionKind.TEXT;
            case MATCHING   -> QuestionKind.MATCHING;
        };
    }
}
