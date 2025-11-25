package com.example.encuestas_api.responses.infrastructure.adapter.out.jpa.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "submission_true_false_answers")
public class TrueFalseAnswerEntity extends SubmissionAnswerEntity {

    @Column(name = "value", nullable = false)
    private boolean value;

    public boolean isValue() { return value; }
    public void setValue(boolean value) { this.value = value; }
}
