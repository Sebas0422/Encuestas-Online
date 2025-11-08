package com.example.encuestas_api.forms.infrastructure.adapter.out.jpa.repository;

import com.example.encuestas_api.forms.infrastructure.adapter.out.jpa.entity.SectionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SectionJpaRepository extends JpaRepository<SectionEntity, Long> {
    List<SectionEntity> findByFormIdOrderByPositionAsc(Long formId);
    int countByFormId(Long formId);
    void deleteByFormIdAndId(Long formId, Long sectionId);
}
