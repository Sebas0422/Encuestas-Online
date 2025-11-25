package com.example.encuestas_api.reports.domain.valueobject;

public class ReportParams {
    private final boolean includeDrafts;

    public ReportParams(boolean includeDrafts) {
        this.includeDrafts = includeDrafts;
    }

    public boolean isIncludeDrafts() { return includeDrafts; }

    public static ReportParams submittedOnly() { return new ReportParams(false); }
    public static ReportParams includeDrafts() { return new ReportParams(true); }
}
