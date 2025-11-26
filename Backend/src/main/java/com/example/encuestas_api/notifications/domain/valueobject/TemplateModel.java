package com.example.encuestas_api.notifications.domain.valueobject;

import java.util.Map;
import java.util.Objects;

public final class TemplateModel {
    private final Map<String, Object> values;

    public TemplateModel(Map<String, Object> values) {
        this.values = values == null ? Map.of() : Map.copyOf(values);
    }
    public Map<String, Object> asMap() { return values; }

    public Object get(String key) { return values.get(key); }
    public String getString(String key) { return Objects.toString(values.get(key), null); }
}
