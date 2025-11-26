package com.example.encuestas_api.forms.infrastructure.adapter.out.jpa;

import com.example.encuestas_api.forms.application.port.out.CountQuestionsByFormPort;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.encuestas_api.questions.infrastructure.adapter.out.jpa.entity.QuestionEntity;

@Component
@Transactional(readOnly = true)
public class CountQuestionsJpaAdapter implements CountQuestionsByFormPort {

    @PersistenceContext
    private EntityManager em;

    @Override
    public long countByFormId(Long formId) {
        Long n = em.createQuery(
                "select count(q) from " + QuestionEntity.class.getSimpleName() + " q where q.formId = :fid",
                Long.class
        ).setParameter("fid", formId).getSingleResult();
        return n != null ? n : 0L;
    }
}
