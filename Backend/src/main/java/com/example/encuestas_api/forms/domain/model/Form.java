package com.example.encuestas_api.forms.domain.model;

import com.example.encuestas_api.forms.domain.exception.InvalidFormStatusTransitionException;
import com.example.encuestas_api.forms.domain.valueobject.AvailabilityWindow;
import com.example.encuestas_api.forms.domain.valueobject.FormTitle;
import com.example.encuestas_api.forms.domain.valueobject.PresentationOptions;
import com.example.encuestas_api.forms.domain.valueobject.ResponseLimitPolicy;

import java.time.Instant;
import java.util.Objects;

public final class Form {
    private final Long id;
    private final Long campaignId;
    private final FormTitle title;
    private final String description;
    private final String coverUrl;
    private final Theme theme;
    private final AccessMode accessMode;
    private final AvailabilityWindow window;
    private final ResponseLimitPolicy limitPolicy;
    private final boolean anonymousMode;
    private final boolean allowEditBeforeSubmit;
    private final boolean autoSave;
    private final PresentationOptions presentation;
    private final FormStatus status;
    private final Instant createdAt;
    private final Instant updatedAt;

    private Form(Long id, Long campaignId, FormTitle title, String description, String coverUrl,
                 Theme theme, AccessMode accessMode, AvailabilityWindow window,
                 ResponseLimitPolicy limitPolicy, boolean anonymousMode, boolean allowEditBeforeSubmit,
                 boolean autoSave, PresentationOptions presentation, FormStatus status,
                 Instant createdAt, Instant updatedAt) {

        if (campaignId == null) throw new IllegalArgumentException("campaignId requerido");
        if (title == null) throw new IllegalArgumentException("title requerido");
        if (theme == null) throw new IllegalArgumentException("theme requerido");
        if (accessMode == null) throw new IllegalArgumentException("accessMode requerido");
        if (limitPolicy == null) throw new IllegalArgumentException("limitPolicy requerido");
        if (presentation == null) throw new IllegalArgumentException("presentation requerido");
        if (status == null) throw new IllegalArgumentException("status requerido");
        if (createdAt == null || updatedAt == null) throw new IllegalArgumentException("timestamps requeridos");

        this.id = id;
        this.campaignId = campaignId;
        this.title = title;
        this.description = description == null ? null : description.trim();
        this.coverUrl = coverUrl;
        this.theme = theme;
        this.accessMode = accessMode;
        this.window = window;
        this.limitPolicy = limitPolicy;
        this.anonymousMode = anonymousMode;
        this.allowEditBeforeSubmit = allowEditBeforeSubmit;
        this.autoSave = autoSave;
        this.presentation = presentation;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Form createNew(Long campaignId, FormTitle title, String description, String coverUrl,
                                 Theme theme, AccessMode mode, AvailabilityWindow window,
                                 ResponseLimitPolicy limitPolicy, boolean anonymousMode,
                                 boolean allowEditBeforeSubmit, boolean autoSave,
                                 PresentationOptions presentation, Instant now) {
        return new Form(null, campaignId, title, description, coverUrl,
                theme == null ? Theme.defaultLight() : theme,
                mode == null ? AccessMode.PUBLIC : mode,
                window, limitPolicy, anonymousMode, allowEditBeforeSubmit, autoSave,
                presentation, FormStatus.draft, now, now);
    }

    public static Form rehydrate(Long id, Long campaignId, FormTitle title, String description, String coverUrl,
                                 Theme theme, AccessMode mode, AvailabilityWindow window,
                                 ResponseLimitPolicy limitPolicy, boolean anonymousMode,
                                 boolean allowEditBeforeSubmit, boolean autoSave,
                                 PresentationOptions presentation, FormStatus status,
                                 Instant createdAt, Instant updatedAt) {
        return new Form(id, campaignId, title, description, coverUrl, theme, mode, window, limitPolicy,
                anonymousMode, allowEditBeforeSubmit, autoSave, presentation, status, createdAt, updatedAt);
    }

    public Form rename(FormTitle newTitle, Instant now) {
        return new Form(id, campaignId, newTitle, description, coverUrl, theme, accessMode, window,
                limitPolicy, anonymousMode, allowEditBeforeSubmit, autoSave, presentation, status, createdAt, now);
    }
    public Form changeDescription(String desc, Instant now) {
        return new Form(id, campaignId, title, desc, coverUrl, theme, accessMode, window,
                limitPolicy, anonymousMode, allowEditBeforeSubmit, autoSave, presentation, status, createdAt, now);
    }
    public Form changeTheme(Theme t, Instant now) {
        return new Form(id, campaignId, title, description, coverUrl, t, accessMode, window,
                limitPolicy, anonymousMode, allowEditBeforeSubmit, autoSave, presentation, status, createdAt, now);
    }
    public Form setAccessMode(AccessMode m, Instant now) {
        return new Form(id, campaignId, title, description, coverUrl, theme, m, window,
                limitPolicy, anonymousMode, allowEditBeforeSubmit, autoSave, presentation, status, createdAt, now);
    }
    public Form reschedule(AvailabilityWindow w, Instant now) {
        return new Form(id, campaignId, title, description, coverUrl, theme, accessMode, w,
                limitPolicy, anonymousMode, allowEditBeforeSubmit, autoSave, presentation, status, createdAt, now);
    }
    public Form setLimitPolicy(ResponseLimitPolicy p, Instant now) {
        return new Form(id, campaignId, title, description, coverUrl, theme, accessMode, window,
                p, anonymousMode, allowEditBeforeSubmit, autoSave, presentation, status, createdAt, now);
    }
    public Form setPresentation(PresentationOptions p, Instant now) {
        return new Form(id, campaignId, title, description, coverUrl, theme, accessMode, window,
                limitPolicy, anonymousMode, allowEditBeforeSubmit, autoSave, p, status, createdAt, now);
    }
    public Form setAnonymous(boolean anonymous, Instant now) {
        return new Form(id, campaignId, title, description, coverUrl, theme, accessMode, window,
                limitPolicy, anonymous, allowEditBeforeSubmit, autoSave, presentation, status, createdAt, now);
    }
    public Form setAllowEditBeforeSubmit(boolean allow, Instant now) {
        return new Form(id, campaignId, title, description, coverUrl, theme, accessMode, window,
                limitPolicy, anonymousMode, allow, autoSave, presentation, status, createdAt, now);
    }
    public Form setAutoSave(boolean enabled, Instant now) {
        return new Form(id, campaignId, title, description, coverUrl, theme, accessMode, window,
                limitPolicy, anonymousMode, allowEditBeforeSubmit, enabled, presentation, status, createdAt, now);
    }

    public Form changeStatus(FormStatus target, Instant now) {
        if (this.status == target) return this;
        if (!isAllowed(this.status, target)) {
            throw new InvalidFormStatusTransitionException(this.status, target);
        }
        return new Form(id, campaignId, title, description, coverUrl, theme, accessMode, window,
                limitPolicy, anonymousMode, allowEditBeforeSubmit, autoSave, presentation, target, createdAt, now);
    }

    private static boolean isAllowed(FormStatus from, FormStatus to) {
        return switch (from) {
            case draft -> (to == FormStatus.published || to == FormStatus.archived);
            case published -> (to == FormStatus.closed || to == FormStatus.archived);
            case closed -> (to == FormStatus.archived);
            case archived -> false;
        };
    }

    public Long getId(){ return id; }
    public Long getCampaignId(){ return campaignId; }
    public FormTitle getTitle(){ return title; }
    public String getDescription(){ return description; }
    public String getCoverUrl(){ return coverUrl; }
    public Theme getTheme(){ return theme; }
    public AccessMode getAccessMode(){ return accessMode; }
    public AvailabilityWindow getWindow(){ return window; }
    public ResponseLimitPolicy getLimitPolicy(){ return limitPolicy; }
    public boolean isAnonymousMode(){ return anonymousMode; }
    public boolean isAllowEditBeforeSubmit(){ return allowEditBeforeSubmit; }
    public boolean isAutoSave(){ return autoSave; }
    public PresentationOptions getPresentation(){ return presentation; }
    public FormStatus getStatus(){ return status; }
    public Instant getCreatedAt(){ return createdAt; }
    public Instant getUpdatedAt(){ return updatedAt; }

    @Override public boolean equals(Object o){
        if (this == o) return true;
        if (!(o instanceof Form f)) return false;
        return id != null && Objects.equals(id, f.id);
    }
    @Override public int hashCode(){ return id == null ? System.identityHashCode(this) : id.hashCode(); }
}
