package com.example.encuestas_api.users.domain.valueobject;

import java.util.Objects;

public final class FullName {
    private final String value;

    private FullName(String value) {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("fullName vacío");
        String trimmed = value.trim();
        if (trimmed.length() > 255)
            throw new IllegalArgumentException("fullName demasiado largo (max 255)");
        this.value = trimmed;
    }

    public static FullName of(String value) {
        return new FullName(value);
    }

    public String getValue() { return value; }

    @Override
    public String toString() { return value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FullName that)) return false;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() { return Objects.hash(value); }
}
