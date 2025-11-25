package com.example.encuestas_api.questions.infrastructure.adapter.out.jpa.repository;

import com.example.encuestas_api.questions.domain.model.QuestionType;
import com.example.encuestas_api.questions.infrastructure.adapter.out.jpa.entity.QuestionEntity;
import org.springframework.data.jpa.domain.Specification;

public final class QuestionSpecifications {
    private QuestionSpecifications(){}

    public static Specification<QuestionEntity> formId(Long id) {
        return (root, q, cb) -> cb.equal(root.get("formId"), id);
    }

    public static Specification<QuestionEntity> sectionId(Long id) {
        if (id == null) return (root, q, cb) -> cb.isNull(root.get("sectionId"));
        return (root, q, cb) -> cb.equal(root.get("sectionId"), id);
    }

    public static Specification<QuestionEntity> type(QuestionType t) {
        if (t == null) return null;
        return (root, q, cb) -> cb.equal(root.get("type"), t);
    }

    public static Specification<QuestionEntity> search(String term) {
        if (term == null || term.isBlank()) return null;
        String like = "%" + term.trim().toLowerCase() + "%";
        return (root, q, cb) -> cb.or(
                cb.like(cb.lower(root.get("prompt")), like),
                cb.like(cb.lower(root.get("helpText")), like)
        );
    }
}
