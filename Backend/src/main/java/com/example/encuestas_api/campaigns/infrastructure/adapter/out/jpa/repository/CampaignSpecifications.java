package com.example.encuestas_api.campaigns.infrastructure.adapter.out.jpa.repository;

import com.example.encuestas_api.campaigns.domain.model.CampaignMemberRole;
import com.example.encuestas_api.campaigns.domain.model.CampaignStatus;
import com.example.encuestas_api.campaigns.infrastructure.adapter.out.jpa.entity.CampaignEntity;
import com.example.encuestas_api.campaigns.infrastructure.adapter.out.jpa.entity.CampaignMemberEntity;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public final class CampaignSpecifications {

    private CampaignSpecifications(){}

    public static Specification<CampaignEntity> searchTerm(String term) {
        if (term == null || term.isBlank()) return null;
        String like = "%" + term.trim().toLowerCase() + "%";
        return (root, q, cb) -> cb.or(
                cb.like(cb.lower(root.get("name")), like),
                cb.like(cb.lower(root.get("description")), like)
        );
    }

    public static Specification<CampaignEntity> statusEquals(CampaignStatus status) {
        if (status == null) return null;
        return (root, q, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<CampaignEntity> startFrom(LocalDate from) {
        if (from == null) return null;
        return (root, q, cb) -> cb.greaterThanOrEqualTo(root.get("startDate"), from);
    }

    public static Specification<CampaignEntity> endTo(LocalDate to) {
        if (to == null) return null;
        return (root, q, cb) -> cb.lessThanOrEqualTo(root.get("endDate"), to);
    }

    public static Specification<CampaignMemberEntity> memberByCampaign(Long campaignId) {
        return (root, q, cb) -> cb.equal(root.get("campaign").get("id"), campaignId);
    }

    public static Specification<CampaignMemberEntity> memberRole(CampaignMemberRole role) {
        if (role == null) return null;
        return (root, q, cb) -> cb.equal(root.get("role"), role);
    }

    public static Specification<CampaignEntity> belongsToUser(Long userId) {
        return (root, query, cb) -> {
            Join<Object, Object> members = root.join("members", JoinType.INNER);
            return cb.equal(members.get("userId"), userId);
        };
    }

}
