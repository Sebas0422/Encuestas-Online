package com.example.encuestas_api.forms.infrastructure.adapter.out.jpa.entity;

import com.example.encuestas_api.forms.domain.model.AccessMode;
import com.example.encuestas_api.forms.domain.model.FormStatus;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "forms", indexes = {
        @Index(name = "idx_forms_campaign", columnList = "campaign_id")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_forms_campaign_title", columnNames = {"campaign_id", "title"})
})
public class FormEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "campaign_id", nullable = false)
    private Long campaignId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "cover_url")
    private String coverUrl;

    @Column(name = "theme_mode", nullable = false, length = 10)
    private String themeMode;

    @Column(name = "theme_primary", length = 20)
    private String themePrimary;

    @Enumerated(EnumType.STRING)
    @Column(name = "access_mode", nullable = false, length = 10)
    private AccessMode accessMode;

    @Column(name = "open_at")
    private Instant openAt;

    @Column(name = "close_at")
    private Instant closeAt;

    @Column(name = "limit_mode", nullable = false, length = 20)
    private String limitMode;

    @Column(name = "limit_n")
    private Integer limitN;

    @Column(name = "anonymous_mode", nullable = false)
    private boolean anonymousMode;

    @Column(name = "allow_edit_before_submit", nullable = false)
    private boolean allowEditBeforeSubmit;

    @Column(name = "auto_save", nullable = false)
    private boolean autoSave;

    @Column(name = "shuffle_questions", nullable = false)
    private boolean shuffleQuestions;

    @Column(name = "shuffle_options", nullable = false)
    private boolean shuffleOptions;

    @Column(name = "progress_bar", nullable = false)
    private boolean progressBar;

    @Column(name = "paginated", nullable = false)
    private boolean paginated;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FormStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    void prePersist() {
        var now = Instant.now();
        if (createdAt == null) createdAt = now;
        if (updatedAt == null) updatedAt = createdAt;
        if (themeMode == null) themeMode = "light";
        if (accessMode == null) accessMode = AccessMode.PUBLIC;
        if (limitMode == null) limitMode = "UNLIMITED";
        title = title == null ? null : title.trim();
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
        title = title == null ? null : title.trim();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCampaignId() { return campaignId; }
    public void setCampaignId(Long campaignId) { this.campaignId = campaignId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }
    public String getThemeMode() { return themeMode; }
    public void setThemeMode(String themeMode) { this.themeMode = themeMode; }
    public String getThemePrimary() { return themePrimary; }
    public void setThemePrimary(String themePrimary) { this.themePrimary = themePrimary; }
    public AccessMode getAccessMode() { return accessMode; }
    public void setAccessMode(AccessMode accessMode) { this.accessMode = accessMode; }
    public Instant getOpenAt() { return openAt; }
    public void setOpenAt(Instant openAt) { this.openAt = openAt; }
    public Instant getCloseAt() { return closeAt; }
    public void setCloseAt(Instant closeAt) { this.closeAt = closeAt; }
    public String getLimitMode() { return limitMode; }
    public void setLimitMode(String limitMode) { this.limitMode = limitMode; }
    public Integer getLimitN() { return limitN; }
    public void setLimitN(Integer limitN) { this.limitN = limitN; }
    public boolean isAnonymousMode() { return anonymousMode; }
    public void setAnonymousMode(boolean anonymousMode) { this.anonymousMode = anonymousMode; }
    public boolean isAllowEditBeforeSubmit() { return allowEditBeforeSubmit; }
    public void setAllowEditBeforeSubmit(boolean allowEditBeforeSubmit) { this.allowEditBeforeSubmit = allowEditBeforeSubmit; }
    public boolean isAutoSave() { return autoSave; }
    public void setAutoSave(boolean autoSave) { this.autoSave = autoSave; }
    public boolean isShuffleQuestions() { return shuffleQuestions; }
    public void setShuffleQuestions(boolean shuffleQuestions) { this.shuffleQuestions = shuffleQuestions; }
    public boolean isShuffleOptions() { return shuffleOptions; }
    public void setShuffleOptions(boolean shuffleOptions) { this.shuffleOptions = shuffleOptions; }
    public boolean isProgressBar() { return progressBar; }
    public void setProgressBar(boolean progressBar) { this.progressBar = progressBar; }
    public boolean isPaginated() { return paginated; }
    public void setPaginated(boolean paginated) { this.paginated = paginated; }
    public FormStatus getStatus() { return status; }
    public void setStatus(FormStatus status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
