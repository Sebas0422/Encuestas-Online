package com.example.encuestas_api.responses.infrastructure.adapter.out.jpa.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "submission_text_answers")
public class TextAnswerEntity extends SubmissionAnswerEntity {

    @Column(name = "text_value", columnDefinition = "text")
    private String text;

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
}
