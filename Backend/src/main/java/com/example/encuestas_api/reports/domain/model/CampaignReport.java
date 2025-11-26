package com.example.encuestas_api.reports.domain.model;

import java.time.Instant;
import java.util.List;

public class CampaignReport {
    private final Long campaignId;
    private final int formsCount;
    private final long totalSubmissions;
    private final long submittedCount;
    private final long draftCount;
    private final double completionRate;
    private final List<FormReport> forms;
    private final Instant generatedAt;

    public CampaignReport(Long campaignId,
                          int formsCount,
                          long totalSubmissions,
                          long submittedCount,
                          long draftCount,
                          double completionRate,
                          List<FormReport> forms,
                          Instant generatedAt) {
        this.campaignId = campaignId;
        this.formsCount = formsCount;
        this.totalSubmissions = totalSubmissions;
        this.submittedCount = submittedCount;
        this.draftCount = draftCount;
        this.completionRate = completionRate;
        this.forms = forms;
        this.generatedAt = generatedAt;
    }

    public Long getCampaignId() { return campaignId; }
    public int getFormsCount() { return formsCount; }
    public long getTotalSubmissions() { return totalSubmissions; }
    public long getSubmittedCount() { return submittedCount; }
    public long getDraftCount() { return draftCount; }
    public double getCompletionRate() { return completionRate; }
    public List<FormReport> getForms() { return forms; }
    public Instant getGeneratedAt() { return generatedAt; }
}
