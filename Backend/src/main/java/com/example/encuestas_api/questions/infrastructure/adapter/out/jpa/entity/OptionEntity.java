package com.example.encuestas_api.questions.infrastructure.adapter.out.jpa.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "question_options", indexes = {
        @Index(name = "idx_qopt_question", columnList = "question_id")
})
public class OptionEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="question_id", nullable=false)
    private Long questionId;

    @Column(nullable=false, length=300)
    private String label;

    @Column(name="is_correct", nullable=false)
    private boolean correct;

    @Column(nullable=false)
    private int position;

    public Long getId() { return id; }
    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    public boolean isCorrect() { return correct; }
    public void setCorrect(boolean correct) { this.correct = correct; }
    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }
}
