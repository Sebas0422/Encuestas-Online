package com.example.encuestas_api.questions.infrastructure.adapter.out.jpa.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "question_matching_pairs", uniqueConstraints = {
        @UniqueConstraint(name = "uk_qmpairs_left", columnNames = {"question_id", "left_id"}),
        @UniqueConstraint(name = "uk_qmpairs_right", columnNames = {"question_id", "right_id"})
}, indexes = {
        @Index(name = "idx_qmpairs_question", columnList = "question_id")
})
public class MatchingPairEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="question_id", nullable=false)
    private Long questionId;

    @Column(name="left_id", nullable=false)
    private Long leftId;

    @Column(name="right_id", nullable=false)
    private Long rightId;

    public Long getId() { return id; }
    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }
    public Long getLeftId() { return leftId; }
    public void setLeftId(Long leftId) { this.leftId = leftId; }
    public Long getRightId() { return rightId; }
    public void setRightId(Long rightId) { this.rightId = rightId; }
}
