package com.example.encuestas_api.responses.infrastructure.adapter.out.jpa.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "submission_answers")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class SubmissionAnswerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", nullable = false)
    protected SubmissionEntity submission;

    @Column(name = "question_id", nullable = false)
    protected Long questionId;

    @Column(name = "question_version")
    protected Integer questionVersion;

    public Long getId() { return id; }
    public SubmissionEntity getSubmission() { return submission; }
    public void setSubmission(SubmissionEntity submission) { this.submission = submission; }
    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }
    public Integer getQuestionVersion() { return questionVersion; }
    public void setQuestionVersion(Integer questionVersion) { this.questionVersion = questionVersion; }
}
