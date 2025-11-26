package com.example.encuestas_api.reports.domain.model;

import com.example.encuestas_api.reports.domain.valueobject.PairKey;

public class MatchingPairStat {
    private final PairKey pair;
    private final long count;

    public MatchingPairStat(PairKey pair, long count) {
        this.pair = pair;
        this.count = count;
    }

    public PairKey getPair() { return pair; }
    public long getCount() { return count; }
}
