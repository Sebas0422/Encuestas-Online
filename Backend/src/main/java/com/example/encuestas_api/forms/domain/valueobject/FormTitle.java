package com.example.encuestas_api.forms.domain.valueobject;

import java.util.Objects;

public final class FormTitle {
    private final String value;
    private FormTitle(String v) {
        if (v == null || v.isBlank()) throw new IllegalArgumentException("Título requerido");
        v = v.trim();
        if (v.length() > 200) throw new IllegalArgumentException("Título excede 200 caracteres");
        this.value = v;
    }
    public static FormTitle of(String v) { return new FormTitle(v); }
    public String getValue() { return value; }
    @Override public String toString() { return value; }
    @Override public boolean equals(Object o){ return (o instanceof FormTitle t) && value.equals(t.value); }
    @Override public int hashCode(){ return Objects.hash(value); }
}
