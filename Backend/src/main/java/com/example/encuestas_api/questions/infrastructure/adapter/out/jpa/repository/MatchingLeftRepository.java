package com.example.encuestas_api.questions.infrastructure.adapter.out.jpa.repository;

import com.example.encuestas_api.questions.infrastructure.adapter.out.jpa.entity.MatchingLeftEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchingLeftRepository extends JpaRepository<MatchingLeftEntity, Long> {
    List<MatchingLeftEntity> findByQuestionIdOrderByPositionAsc(Long questionId);
    void deleteByQuestionId(Long questionId);
}
