package com.example.encuestas_api.reports.domain.model;

import java.time.Instant;
import java.util.List;

public class FormReport {
    private final Long formId;
    private final long totalSubmissions;
    private final long submittedCount;
    private final long draftCount;
    private final double completionRate;
    private final List<QuestionReport> questions;
    private final Instant generatedAt;

    public FormReport(Long formId,
                      long totalSubmissions,
                      long submittedCount,
                      long draftCount,
                      double completionRate,
                      List<QuestionReport> questions,
                      Instant generatedAt) {
        this.formId = formId;
        this.totalSubmissions = totalSubmissions;
        this.submittedCount = submittedCount;
        this.draftCount = draftCount;
        this.completionRate = completionRate;
        this.questions = questions;
        this.generatedAt = generatedAt;
    }

    public Long getFormId() { return formId; }
    public long getTotalSubmissions() { return totalSubmissions; }
    public long getSubmittedCount() { return submittedCount; }
    public long getDraftCount() { return draftCount; }
    public double getCompletionRate() { return completionRate; }
    public List<QuestionReport> getQuestions() { return questions; }
    public Instant getGeneratedAt() { return generatedAt; }
}
