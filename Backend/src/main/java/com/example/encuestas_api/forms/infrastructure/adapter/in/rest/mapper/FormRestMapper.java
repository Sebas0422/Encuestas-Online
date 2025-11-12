package com.example.encuestas_api.forms.infrastructure.adapter.in.rest.mapper;

import com.example.encuestas_api.forms.domain.model.Form;
import com.example.encuestas_api.forms.domain.model.Section;
import com.example.encuestas_api.forms.infrastructure.adapter.in.rest.dto.FormResponse;
import com.example.encuestas_api.forms.infrastructure.adapter.in.rest.dto.SectionResponse;

public final class FormRestMapper {
    private FormRestMapper(){}

    public static FormResponse toResponse(Form f) {
        return new FormResponse(
                f.getId(), f.getCampaignId(), f.getTitle().getValue(), f.getDescription(), f.getCoverUrl(),
                f.getTheme().mode(), f.getTheme().primaryColor(), f.getAccessMode(),
                f.getWindow() == null ? null : f.getWindow().openAt(),
                f.getWindow() == null ? null : f.getWindow().closeAt(),
                f.getLimitPolicy().mode().name(),
                f.getLimitPolicy().maxResponsesPerUser(),
                f.isAnonymousMode(), f.isAllowEditBeforeSubmit(), f.isAutoSave(),
                f.getPresentation().shuffleQuestions(), f.getPresentation().shuffleOptions(),
                f.getPresentation().progressBar(), f.getPresentation().paginated(),
                f.getStatus(), f.getCreatedAt(), f.getUpdatedAt()
        );
    }

    public static SectionResponse toResponse(Section s) {
        return new SectionResponse(s.getId(), s.getFormId(), s.getTitle(), s.getPosition());
    }
}
