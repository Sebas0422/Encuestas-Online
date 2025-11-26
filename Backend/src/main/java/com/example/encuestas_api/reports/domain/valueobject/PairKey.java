package com.example.encuestas_api.reports.domain.valueobject;

import java.util.Objects;

public class PairKey {
    private final Long leftId;
    private final Long rightId;

    public PairKey(Long leftId, Long rightId) {
        this.leftId = leftId;
        this.rightId = rightId;
    }

    public Long getLeftId() { return leftId; }
    public Long getRightId() { return rightId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PairKey)) return false;
        PairKey that = (PairKey) o;
        return Objects.equals(leftId, that.leftId) && Objects.equals(rightId, that.rightId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(leftId, rightId);
    }
}
