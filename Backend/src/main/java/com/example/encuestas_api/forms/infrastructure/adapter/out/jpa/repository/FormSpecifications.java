package com.example.encuestas_api.forms.infrastructure.adapter.out.jpa.repository;

import com.example.encuestas_api.forms.domain.model.AccessMode;
import com.example.encuestas_api.forms.domain.model.FormStatus;
import com.example.encuestas_api.forms.infrastructure.adapter.out.jpa.entity.FormEntity;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;

public final class FormSpecifications {
    private FormSpecifications(){}

    public static Specification<FormEntity> campaignId(Long id) {
        if (id == null) return null;
        return (root, q, cb) -> cb.equal(root.get("campaignId"), id);
    }

    public static Specification<FormEntity> searchTerm(String term) {
        if (term == null || term.isBlank()) return null;
        String like = "%" + term.trim().toLowerCase() + "%";
        return (root, q, cb) -> cb.or(
                cb.like(cb.lower(root.get("title")), like),
                cb.like(cb.lower(root.get("description")), like)
        );
    }

    public static Specification<FormEntity> status(FormStatus s) {
        if (s == null) return null;
        return (root, q, cb) -> cb.equal(root.get("status"), s);
    }

    public static Specification<FormEntity> accessMode(AccessMode m) {
        if (m == null) return null;
        return (root, q, cb) -> cb.equal(root.get("accessMode"), m);
    }

    public static Specification<FormEntity> openFrom(Instant from) {
        if (from == null) return null;
        return (root, q, cb) -> cb.greaterThanOrEqualTo(root.get("openAt"), from);
    }

    public static Specification<FormEntity> closeTo(Instant to) {
        if (to == null) return null;
        return (root, q, cb) -> cb.lessThanOrEqualTo(root.get("closeAt"), to);
    }
}
