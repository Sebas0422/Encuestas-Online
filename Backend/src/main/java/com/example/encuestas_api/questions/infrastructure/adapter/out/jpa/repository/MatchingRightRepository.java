package com.example.encuestas_api.questions.infrastructure.adapter.out.jpa.repository;

import com.example.encuestas_api.questions.infrastructure.adapter.out.jpa.entity.MatchingRightEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchingRightRepository extends JpaRepository<MatchingRightEntity, Long> {
    List<MatchingRightEntity> findByQuestionIdOrderByPositionAsc(Long questionId);
    void deleteByQuestionId(Long questionId);
}
