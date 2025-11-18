package com.example.encuestas_api.forms.domain.model;

import java.util.Objects;

public final class Section {
    private final Long id;
    private final Long formId;
    private final String title;
    private final int position;

    private Section(Long id, Long formId, String title, int position) {
        if (formId == null) throw new IllegalArgumentException("formId requerido");
        if (position < 0) throw new IllegalArgumentException("position >= 0");
        this.id = id; this.formId = formId; this.title = title == null ? null : title.trim(); this.position = position;
    }
    public static Section newOf(Long formId, String title, int position) { return new Section(null, formId, title, position); }
    public static Section rehydrate(Long id, Long formId, String title, int position){ return new Section(id, formId, title, position); }

    public Long getId(){ return id; } public Long getFormId(){ return formId; }
    public String getTitle(){ return title; } public int getPosition(){ return position; }

    @Override public boolean equals(Object o){ return (o instanceof Section s) && id != null && Objects.equals(id, s.id); }
    @Override public int hashCode(){ return id == null ? 0 : id.hashCode(); }
}
