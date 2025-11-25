package com.example.encuestas_api.responses.infrastructure.adapter.out.jpa.repository;

import com.example.encuestas_api.responses.infrastructure.adapter.out.jpa.entity.SubmissionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmissionJpaRepository extends JpaRepository<SubmissionEntity, Long> {

    Page<SubmissionEntity> findByFormId(Long formId, Pageable pageable);
    Page<SubmissionEntity> findByFormIdAndStatus(Long formId, String status, Pageable pageable);

    boolean existsByFormIdAndStatusAndRespondentUserId(Long formId, String status, Long userId);
    boolean existsByFormIdAndStatusAndRespondentEmail(Long formId, String status, String email);
    boolean existsByFormIdAndStatusAndRespondentCode(Long formId, String status, String code);

    long countByFormIdAndStatus(Long formId, String status);
}
