package com.example.encuestas_api.questions.infrastructure.adapter.out.jpa.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "question_matching_left", indexes = {
        @Index(name = "idx_qmleft_question", columnList = "question_id")
})
public class MatchingLeftEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="question_id", nullable=false)
    private Long questionId;

    @Column(nullable=false, length=300)
    private String text;

    @Column(nullable=false)
    private int position;

    public Long getId() { return id; }
    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }
}
