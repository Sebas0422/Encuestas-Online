package com.example.encuestas_api.forms.infrastructure.adapter.out.jpa.mapper;

import com.example.encuestas_api.forms.domain.model.Form;
import com.example.encuestas_api.forms.domain.model.Theme;
import com.example.encuestas_api.forms.domain.valueobject.AvailabilityWindow;
import com.example.encuestas_api.forms.domain.valueobject.FormTitle;
import com.example.encuestas_api.forms.domain.valueobject.PresentationOptions;
import com.example.encuestas_api.forms.domain.valueobject.ResponseLimitPolicy;
import com.example.encuestas_api.forms.infrastructure.adapter.out.jpa.entity.FormEntity;

public final class FormJpaMapper {
    private FormJpaMapper(){}

    public static Form toDomain(FormEntity e) {
        var window = AvailabilityWindow.of(e.getOpenAt(), e.getCloseAt());
        var limit = switch (e.getLimitMode()) {
            case "ONE_PER_USER" -> ResponseLimitPolicy.onePerUser();
            case "LIMITED_N"    -> ResponseLimitPolicy.limitedN(e.getLimitN() == null ? 1 : e.getLimitN());
            default             -> ResponseLimitPolicy.unlimited();
        };
        var theme = new Theme(e.getThemeMode(), e.getThemePrimary());
        var presentation = PresentationOptions.of(e.isShuffleQuestions(), e.isShuffleOptions(), e.isProgressBar(), e.isPaginated());
        return Form.rehydrate(
                e.getId(), e.getCampaignId(), FormTitle.of(e.getTitle()), e.getDescription(), e.getCoverUrl(),
                 theme, e.getAccessMode(), window, e.getPublicCode(),limit, e.isAnonymousMode(), e.isAllowEditBeforeSubmit(),
                e.isAutoSave(), presentation, e.getStatus(), e.getCreatedAt(), e.getUpdatedAt()
        );
    }

    public static FormEntity toEntity(Form d) {
        var e = new FormEntity();
        e.setId(d.getId());
        e.setCampaignId(d.getCampaignId());
        e.setTitle(d.getTitle().getValue());
        e.setDescription(d.getDescription());
        e.setCoverUrl(d.getCoverUrl());
        e.setThemeMode(d.getTheme().mode());
        e.setThemePrimary(d.getTheme().primaryColor());
        e.setAccessMode(d.getAccessMode());
        e.setOpenAt(d.getWindow() == null ? null : d.getWindow().openAt());
        e.setCloseAt(d.getWindow() == null ? null : d.getWindow().closeAt());
        e.setPublicCode(d.getPublicCode());

        var lm = d.getLimitPolicy().mode();
        switch (lm) {
            case ONE_PER_USER -> { e.setLimitMode("ONE_PER_USER"); e.setLimitN(null); }
            case LIMITED_N -> { e.setLimitMode("LIMITED_N"); e.setLimitN(d.getLimitPolicy().maxResponsesPerUser()); }
            default -> { e.setLimitMode("UNLIMITED"); e.setLimitN(null); }
        }

        e.setAnonymousMode(d.isAnonymousMode());
        e.setAllowEditBeforeSubmit(d.isAllowEditBeforeSubmit());
        e.setAutoSave(d.isAutoSave());

        e.setShuffleQuestions(d.getPresentation().shuffleQuestions());
        e.setShuffleOptions(d.getPresentation().shuffleOptions());
        e.setProgressBar(d.getPresentation().progressBar());
        e.setPaginated(d.getPresentation().paginated());

        e.setStatus(d.getStatus());
        e.setCreatedAt(d.getCreatedAt());
        e.setUpdatedAt(d.getUpdatedAt());
        return e;
    }
}
