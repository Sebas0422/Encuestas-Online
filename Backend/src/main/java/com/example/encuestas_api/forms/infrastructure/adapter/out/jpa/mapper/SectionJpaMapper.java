package com.example.encuestas_api.forms.infrastructure.adapter.out.jpa.mapper;

import com.example.encuestas_api.forms.domain.model.Section;
import com.example.encuestas_api.forms.infrastructure.adapter.out.jpa.entity.SectionEntity;

public final class SectionJpaMapper {
    private SectionJpaMapper(){}

    public static Section toDomain(SectionEntity e) {
        return Section.rehydrate(e.getId(), e.getFormId(), e.getTitle(), e.getPosition());
    }

    public static SectionEntity toEntity(Section d) {
        var e = new SectionEntity();
        e.setId(d.getId());
        e.setFormId(d.getFormId());
        e.setTitle(d.getTitle());
        e.setPosition(d.getPosition());
        return e;
    }
}
