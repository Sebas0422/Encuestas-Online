package com.example.encuestas_api.responses.domain.valueobject;

public class ChoiceOptionSnapshot {
    private final Long id;
    private final String label;

    public ChoiceOptionSnapshot(Long id, String label) {
        this.id = id;
        this.label = label;
    }

    public Long getId() { return id; }
    public String getLabel() { return label; }
}
