package com.example.encuestas_api.users.domain.valueobject;

import java.util.Objects;
import java.util.regex.Pattern;

public final class Email {
    private static final Pattern SIMPLE =
            Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final String value;

    private Email(String value) {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("email vacío");
        String trimmed = value.trim().toLowerCase();
        if (!SIMPLE.matcher(trimmed).matches())
            throw new IllegalArgumentException("email inválido");
        this.value = trimmed;
    }

    public static Email of(String value) {
        return new Email(value);
    }

    public String getValue() { return value; }

    @Override
    public String toString() { return value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Email email)) return false;
        return value.equals(email.value);
    }

    @Override
    public int hashCode() { return Objects.hash(value); }
}
