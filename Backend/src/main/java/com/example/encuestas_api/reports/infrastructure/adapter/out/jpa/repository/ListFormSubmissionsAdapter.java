package com.example.encuestas_api.reports.infrastructure.adapter.out.jpa.repository;

import com.example.encuestas_api.reports.application.port.out.ListFormSubmissionsPort;
import com.example.encuestas_api.responses.domain.model.Submission;
import com.example.encuestas_api.responses.infrastructure.adapter.out.jpa.entity.SubmissionEntity;
import com.example.encuestas_api.responses.infrastructure.adapter.out.jpa.mapper.ResponsesJpaMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Transactional(readOnly = true)
public class ListFormSubmissionsAdapter implements ListFormSubmissionsPort {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Submission> findByFormId(Long formId) {
        List<SubmissionEntity> entities = em.createQuery("""
                select s
                from com.example.encuestas_api.responses.infrastructure.adapter.out.jpa.entity.SubmissionEntity s
                where s.formId = :fid
                order by s.id asc
                """, SubmissionEntity.class)
                .setParameter("fid", formId)
                .getResultList();

        return entities.stream()
                .map(ResponsesJpaMapper::toDomain)
                .toList();
    }
}
