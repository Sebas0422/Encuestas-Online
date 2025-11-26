package com.example.encuestas_api.questions.domain.valueobject;

import java.util.Objects;

public final class Prompt {
    private final String value;
    private Prompt(String v) {
        if (v == null || v.isBlank()) throw new IllegalArgumentException("Prompt requerido");
        v = v.trim();
        if (v.length() > 500) throw new IllegalArgumentException("Prompt excede 500 caracteres");
        this.value = v;
    }
    public static Prompt of(String v){ return new Prompt(v); }
    public String getValue(){ return value; }
    @Override public String toString(){ return value; }
    @Override public boolean equals(Object o){ return (o instanceof Prompt p) && value.equals(p.value); }
    @Override public int hashCode(){ return Objects.hash(value); }
}
