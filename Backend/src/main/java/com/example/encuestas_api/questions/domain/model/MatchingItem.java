package com.example.encuestas_api.questions.domain.model;

import java.util.Objects;

public final class MatchingItem {
    private final Long id;
    private final String text;

    private MatchingItem(Long id, String text) {
        if (text == null || text.isBlank()) throw new IllegalArgumentException("Texto requerido");
        String t = text.trim();
        if (t.length() > 300) throw new IllegalArgumentException("Texto excede 300 caracteres");
        this.id = id; this.text = t;
    }
    public static MatchingItem newOf(String text){ return new MatchingItem(null, text); }
    public static MatchingItem rehydrate(Long id, String text){ return new MatchingItem(id, text); }

    public Long id(){ return id; }
    public String text(){ return text; }

    @Override public boolean equals(Object o){ return (o instanceof MatchingItem m) && Objects.equals(id, m.id); }
    @Override public int hashCode(){ return id == null ? 0 : id.hashCode(); }
}
