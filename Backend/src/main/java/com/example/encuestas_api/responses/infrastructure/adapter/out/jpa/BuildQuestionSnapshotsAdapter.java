package com.example.encuestas_api.responses.infrastructure.adapter.out.jpa;

import com.example.encuestas_api.questions.domain.model.QuestionType;
import com.example.encuestas_api.questions.domain.model.TextMode;
import com.example.encuestas_api.questions.infrastructure.adapter.out.jpa.entity.MatchingLeftEntity;
import com.example.encuestas_api.questions.infrastructure.adapter.out.jpa.entity.MatchingRightEntity;
import com.example.encuestas_api.questions.infrastructure.adapter.out.jpa.entity.OptionEntity;
import com.example.encuestas_api.questions.infrastructure.adapter.out.jpa.entity.QuestionEntity;
import com.example.encuestas_api.responses.application.port.out.BuildQuestionSnapshotsPort;
import com.example.encuestas_api.responses.domain.valueobject.QuestionKind;
import com.example.encuestas_api.responses.domain.valueobject.QuestionSnapshot;
import com.example.encuestas_api.responses.domain.valueobject.SelectionMode;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Transactional(readOnly = true)
public class BuildQuestionSnapshotsAdapter implements BuildQuestionSnapshotsPort {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Map<Long, QuestionSnapshot> byFormId(Long formId) {

        List<QuestionEntity> questions = em.createQuery("""
                select q
                from QuestionEntity q
                where q.formId = :fid
                order by q.position asc
                """, QuestionEntity.class)
                .setParameter("fid", formId)
                .getResultList();

        Map<Long, QuestionSnapshot> snapshots = new LinkedHashMap<>();

        for (QuestionEntity q : questions) {
            QuestionSnapshot.Builder b = new QuestionSnapshot.Builder()
                    .questionId(q.getId())
                    .required(q.isRequired())
                    .kind(mapKind(q.getType()));

            if (q.getType() == QuestionType.CHOICE) {
                List<Long> optionIds = em.createQuery("""
                            select o
                            from OptionEntity o
                            where o.questionId = :qid
                            order by o.position asc
                            """, OptionEntity.class)
                        .setParameter("qid", q.getId())
                        .getResultList()
                        .stream()
                        .map(OptionEntity::getId)
                        .collect(Collectors.toList());

                b.choice(
                        mapSelectionMode(q.getSelectionMode()),
                        q.getMinSelections(),
                        q.getMaxSelections(),
                        optionIds
                );
            }
            else if (q.getType() == QuestionType.TEXT) {
                b.text(
                        mapTextMode(q.getTextMode()),
                        q.getMinLength(),
                        q.getMaxLength()
                );
            }
            else if (q.getType() == QuestionType.MATCHING) {
                List<Long> leftIds = em.createQuery("""
                            select m
                            from MatchingLeftEntity m
                            where m.questionId = :qid
                            order by m.id asc
                            """, MatchingLeftEntity.class)
                        .setParameter("qid", q.getId())
                        .getResultList()
                        .stream()
                        .map(MatchingLeftEntity::getId)
                        .collect(Collectors.toList());

                List<Long> rightIds = em.createQuery("""
                            select m
                            from MatchingRightEntity m
                            where m.questionId = :qid
                            order by m.id asc
                            """, MatchingRightEntity.class)
                        .setParameter("qid", q.getId())
                        .getResultList()
                        .stream()
                        .map(MatchingRightEntity::getId)
                        .collect(Collectors.toList());

                b.matching(leftIds, rightIds);
            }

            snapshots.put(q.getId(), b.build());
        }

        return snapshots;
    }

    private QuestionKind mapKind(QuestionType t) {
        return switch (t) {
            case CHOICE     -> QuestionKind.CHOICE;
            case TRUE_FALSE -> QuestionKind.TRUE_FALSE;
            case TEXT       -> QuestionKind.TEXT;
            case MATCHING   -> QuestionKind.MATCHING;
        };
    }


    private SelectionMode mapSelectionMode(String mode) {
        if (mode == null) return SelectionMode.SINGLE;
        return "MULTI".equalsIgnoreCase(mode) ? SelectionMode.MULTI : SelectionMode.SINGLE;
    }

    private TextMode mapTextMode(String mode) {
        if (mode == null) return TextMode.SHORT;
        return "LONG".equalsIgnoreCase(mode) ? TextMode.LONG : TextMode.SHORT;
    }
}
