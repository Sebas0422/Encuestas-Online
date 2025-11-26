package com.example.encuestas_api.reports.domain.model;

public class ChoiceOptionStat {
    private final Long optionId;
    private final long count;

    public ChoiceOptionStat(Long optionId, long count) {
        this.optionId = optionId;
        this.count = count;
    }

    public Long getOptionId() { return optionId; }
    public long getCount() { return count; }
}
