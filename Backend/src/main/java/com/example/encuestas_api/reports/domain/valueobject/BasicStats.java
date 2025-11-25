package com.example.encuestas_api.reports.domain.valueobject;

public class BasicStats {
    private final long total;
    private final long answered;
    private final long omitted;

    public BasicStats(long total, long answered, long omitted) {
        this.total = total;
        this.answered = answered;
        this.omitted = omitted;
    }

    public long getTotal() { return total; }
    public long getAnswered() { return answered; }
    public long getOmitted() { return omitted; }

    public double completionRate() {
        return total == 0 ? 0.0 : (double) answered / (double) total;
    }
}
