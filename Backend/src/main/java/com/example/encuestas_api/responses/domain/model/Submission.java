package com.example.encuestas_api.responses.domain.model;

import com.example.encuestas_api.responses.domain.exception.EditNotAllowedException;
import com.example.encuestas_api.responses.domain.exception.SubmissionAlreadySubmittedException;
import com.example.encuestas_api.responses.domain.valueobject.Respondent;

import java.time.Instant;
import java.util.*;

public class Submission {

    private Long id;
    private final Long formId;
    private final Respondent respondent;

    private String sourceIp;

    private SubmissionStatus status;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant submittedAt;

    private final Map<Long, SubmissionAnswer> answers = new LinkedHashMap<>(); // key: questionId

    public Submission(Long formId, Respondent respondent) {
        this.formId = Objects.requireNonNull(formId, "formId");
        this.respondent = Objects.requireNonNull(respondent, "respondent");
        this.status = SubmissionStatus.DRAFT;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public void addOrReplaceAnswer(SubmissionAnswer answer) {
        ensureEditable();
        Objects.requireNonNull(answer, "answer");
        answers.put(answer.getQuestionId(), answer);
        touch();
    }

    public void removeAnswer(Long questionId) {
        ensureEditable();
        answers.remove(questionId);
        touch();
    }

    public void markSubmitted() {
        if (this.status == SubmissionStatus.SUBMITTED) {
            throw new SubmissionAlreadySubmittedException("Submission ya fue enviada");
        }
        this.status = SubmissionStatus.SUBMITTED;
        this.submittedAt = Instant.now();
        touch();
    }

    private void ensureEditable() {
        if (this.status == SubmissionStatus.SUBMITTED) {
            throw new EditNotAllowedException("No se puede editar una submission enviada");
        }
    }

    private void touch() { this.updatedAt = Instant.now(); }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getFormId() { return formId; }
    public Respondent getRespondent() { return respondent; }

    public String getSourceIp() { return sourceIp; }
    public void setSourceIp(String sourceIp) { this.sourceIp = sourceIp; }

    public SubmissionStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public Instant getSubmittedAt() { return submittedAt; }

    public Collection<SubmissionAnswer> getAnswers() { return answers.values(); }
    public Optional<SubmissionAnswer> findAnswer(Long questionId) { return Optional.ofNullable(answers.get(questionId)); }
}
