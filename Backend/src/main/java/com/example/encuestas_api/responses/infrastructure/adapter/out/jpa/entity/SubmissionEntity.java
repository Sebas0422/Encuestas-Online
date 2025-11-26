package com.example.encuestas_api.responses.infrastructure.adapter.out.jpa.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "submissions")
public class SubmissionEntity {

    public enum RespondentType { ANONYMOUS, USER, EMAIL, CODE }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "form_id", nullable = false)
    private Long formId;

    @Enumerated(EnumType.STRING)
    @Column(name = "respondent_type", nullable = false, length = 12)
    private RespondentType respondentType;

    @Column(name = "respondent_user_id")
    private Long respondentUserId;

    @Column(name = "respondent_email")
    private String respondentEmail;

    @Column(name = "respondent_code")
    private String respondentCode;

    @Column(name = "source_ip")
    private String sourceIp;

    @Column(name = "status", nullable = false, length = 16) // DRAFT | SUBMITTED
    private String status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "submitted_at")
    private Instant submittedAt;

    @OneToMany(mappedBy = "submission", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubmissionAnswerEntity> answers = new ArrayList<>();

    public Long getId() { return id; }
    public Long getFormId() { return formId; }
    public void setFormId(Long formId) { this.formId = formId; }
    public RespondentType getRespondentType() { return respondentType; }
    public void setRespondentType(RespondentType respondentType) { this.respondentType = respondentType; }
    public Long getRespondentUserId() { return respondentUserId; }
    public void setRespondentUserId(Long respondentUserId) { this.respondentUserId = respondentUserId; }
    public String getRespondentEmail() { return respondentEmail; }
    public void setRespondentEmail(String respondentEmail) { this.respondentEmail = respondentEmail; }
    public String getRespondentCode() { return respondentCode; }
    public void setRespondentCode(String respondentCode) { this.respondentCode = respondentCode; }
    public String getSourceIp() { return sourceIp; }
    public void setSourceIp(String sourceIp) { this.sourceIp = sourceIp; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
    public Instant getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(Instant submittedAt) { this.submittedAt = submittedAt; }
    public List<SubmissionAnswerEntity> getAnswers() { return answers; }
    public void setAnswers(List<SubmissionAnswerEntity> answers) { this.answers = answers; }

    public void setId(Long id) {
        this.id = id;
    }
}
