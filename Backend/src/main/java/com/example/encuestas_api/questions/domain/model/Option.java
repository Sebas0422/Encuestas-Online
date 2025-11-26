package com.example.encuestas_api.questions.domain.model;

import java.util.Objects;

public final class Option {
    private final Long id;
    private final String label;
    private final boolean correct;

    private Option(Long id, String label, boolean correct) {
        if (label == null || label.isBlank()) throw new IllegalArgumentException("Opción sin texto");
        String t = label.trim();
        if (t.length() > 300) throw new IllegalArgumentException("Opción excede 300 caracteres");
        this.id = id; this.label = t; this.correct = correct;
    }
    public static Option newOf(String label, boolean correct){ return new Option(null, label, correct); }
    public static Option rehydrate(Long id, String label, boolean correct){ return new Option(id, label, correct); }

    public Long id(){ return id; }
    public String label(){ return label; }
    public boolean correct(){ return correct; }

    @Override public boolean equals(Object o){ return (o instanceof Option other) && Objects.equals(id, other.id); }
    @Override public int hashCode(){ return id == null ? 0 : id.hashCode(); }
}
