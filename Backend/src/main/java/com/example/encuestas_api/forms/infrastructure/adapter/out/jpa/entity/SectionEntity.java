package com.example.encuestas_api.forms.infrastructure.adapter.out.jpa.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "form_sections", indexes = {
        @Index(name = "idx_sections_form", columnList = "form_id")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_section_position", columnNames = {"form_id", "position"})
})
public class SectionEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "form_id", nullable = false)
    private Long formId;

    @Column(length = 200)
    private String title;

    @Column(nullable = false)
    private int position;

    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public Long getFormId() { return formId; } public void setFormId(Long formId) { this.formId = formId; }
    public String getTitle() { return title; } public void setTitle(String title) { this.title = title; }
    public int getPosition() { return position; } public void setPosition(int position) { this.position = position; }
}
