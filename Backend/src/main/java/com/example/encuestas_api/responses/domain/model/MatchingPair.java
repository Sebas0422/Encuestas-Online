package com.example.encuestas_api.responses.domain.model;

import java.util.Objects;

public class MatchingPair {
    private final Long leftId;
    private final Long rightId;

    public MatchingPair(Long leftId, Long rightId) {
        this.leftId = Objects.requireNonNull(leftId, "leftId");
        this.rightId = Objects.requireNonNull(rightId, "rightId");
    }

    public Long getLeftId() { return leftId; }
    public Long getRightId() { return rightId; }
}
