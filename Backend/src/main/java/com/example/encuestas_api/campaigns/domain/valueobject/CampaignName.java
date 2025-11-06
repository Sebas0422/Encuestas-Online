package com.example.encuestas_api.campaigns.domain.valueobject;

import java.util.Objects;

public final class CampaignName {
    private final String value;

    private CampaignName(String value) {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("El nombre de la campaign es requerido");
        String v = value.trim();
        if (v.length() > 200)
            throw new IllegalArgumentException("El nombre de la campaign excede 200 caracteres");
        this.value = v;
    }

    public static CampaignName of(String value) { return new CampaignName(value); }

    public String getValue() { return value; }

    @Override public String toString() { return value; }
    @Override public boolean equals(Object o) { return (o instanceof CampaignName c) && value.equals(c.value); }
    @Override public int hashCode() { return Objects.hash(value); }
}
