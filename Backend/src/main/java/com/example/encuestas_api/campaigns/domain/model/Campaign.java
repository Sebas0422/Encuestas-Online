package com.example.encuestas_api.campaigns.domain.model;

import com.example.encuestas_api.campaigns.domain.exception.InvalidCampaignDatesException;
import com.example.encuestas_api.campaigns.domain.exception.InvalidCampaignStatusTransitionException;
import com.example.encuestas_api.campaigns.domain.valueobject.CampaignName;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

public final class Campaign {

    private final Long id;
    private final CampaignName name;
    private final String description;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final CampaignStatus status;
    private final Instant createdAt;
    private final Instant updatedAt;

    private Campaign(Long id,
                     CampaignName name,
                     String description,
                     LocalDate startDate,
                     LocalDate endDate,
                     CampaignStatus status,
                     Instant createdAt,
                     Instant updatedAt) {

        if (name == null) throw new IllegalArgumentException("name requerido");
        if (status == null) throw new IllegalArgumentException("status requerido");
        if (createdAt == null || updatedAt == null)
            throw new IllegalArgumentException("timestamps requeridos");

        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            throw new InvalidCampaignDatesException("endDate no puede ser anterior a startDate");
        }

        this.id = id;
        this.name = name;
        this.description = description == null ? null : description.trim();
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Campaign createNew(CampaignName name, String description,
                                     LocalDate startDate, LocalDate endDate, Instant now) {
        return new Campaign(null, name, description, startDate, endDate, CampaignStatus.draft, now, now);
    }

    public static Campaign rehydrate(Long id, CampaignName name, String description,
                                     LocalDate startDate, LocalDate endDate,
                                     CampaignStatus status, Instant createdAt, Instant updatedAt) {
        return new Campaign(id, name, description, startDate, endDate, status, createdAt, updatedAt);
    }

    public Campaign rename(CampaignName newName, Instant now) {
        return new Campaign(id, newName, description, startDate, endDate, status, createdAt, now);
    }

    public Campaign changeDescription(String newDescription, Instant now) {
        return new Campaign(id, name, newDescription, startDate, endDate, status, createdAt, now);
    }

    public Campaign reschedule(LocalDate newStart, LocalDate newEnd, Instant now) {
        if (newStart != null && newEnd != null && newEnd.isBefore(newStart)) {
            throw new InvalidCampaignDatesException("endDate no puede ser anterior a startDate");
        }
        return new Campaign(id, name, description, newStart, newEnd, status, createdAt, now);
    }

    public Campaign changeStatus(CampaignStatus target, Instant now) {
        if (this.status == target) return this;
        if (!isAllowedTransition(this.status, target)) {
            throw new InvalidCampaignStatusTransitionException(this.status, target);
        }
        return new Campaign(id, name, description, startDate, endDate, target, createdAt, now);
    }

    private static boolean isAllowedTransition(CampaignStatus from, CampaignStatus to) {
        return switch (from) {
            case draft -> (to == CampaignStatus.active || to == CampaignStatus.archived);
            case active -> (to == CampaignStatus.closed || to == CampaignStatus.archived);
            case closed -> (to == CampaignStatus.archived);
            case archived -> false;
        };
    }

    public Long getId() { return id; }
    public CampaignName getName() { return name; }
    public String getDescription() { return description; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public CampaignStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Campaign that)) return false;
        return id != null && Objects.equals(id, that.id);
    }
    @Override public int hashCode() { return id == null ? System.identityHashCode(this) : id.hashCode(); }
}
