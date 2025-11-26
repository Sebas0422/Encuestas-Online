package com.example.encuestas_api.questions.infrastructure.adapter.out.jpa.entity;

import com.example.encuestas_api.questions.domain.model.QuestionType;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "questions", indexes = {
        @Index(name = "idx_questions_form", columnList = "form_id"),
        @Index(name = "idx_questions_section", columnList = "section_id")
})
public class QuestionEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="form_id", nullable=false)
    private Long formId;

    @Column(name="section_id")
    private Long sectionId;

    @Column(nullable=false)
    private int position;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=20)
    private QuestionType type;

    @Column(nullable=false, length=500)
    private String prompt;

    @Column(name="help_text", length=1000)
    private String helpText;

    @Column(nullable=false)
    private boolean required;

    @Column(name="shuffle_options", nullable=false)
    private boolean shuffleOptions;

    @Column(name="selection_mode", length=10) // SINGLE | MULTI
    private String selectionMode;

    @Column(name="min_selections")
    private Integer minSelections;

    @Column(name="max_selections")
    private Integer maxSelections;

    @Column(name="text_mode", length=10) // SHORT | LONG
    private String textMode;

    @Column(name="placeholder", length=300)
    private String placeholder;

    @Column(name="min_length")
    private Integer minLength;

    @Column(name="max_length")
    private Integer maxLength;

    @Column(name="created_at", nullable=false, updatable=false)
    private Instant createdAt;

    @Column(name="updated_at", nullable=false)
    private Instant updatedAt;

    @PrePersist
    void prePersist() {
        var now = Instant.now();
        if (createdAt == null) createdAt = now;
        if (updatedAt == null) updatedAt = createdAt;
        if (prompt != null) prompt = prompt.trim();
        if (helpText != null) helpText = helpText.trim();
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
        if (prompt != null) prompt = prompt.trim();
        if (helpText != null) helpText = helpText.trim();
    }

    public Long getId() { return id; }
    public Long getFormId() { return formId; }
    public void setFormId(Long formId) { this.formId = formId; }
    public Long getSectionId() { return sectionId; }
    public void setSectionId(Long sectionId) { this.sectionId = sectionId; }
    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }
    public QuestionType getType() { return type; }
    public void setType(QuestionType type) { this.type = type; }
    public String getPrompt() { return prompt; }
    public void setPrompt(String prompt) { this.prompt = prompt; }
    public String getHelpText() { return helpText; }
    public void setHelpText(String helpText) { this.helpText = helpText; }
    public boolean isRequired() { return required; }
    public void setRequired(boolean required) { this.required = required; }
    public boolean isShuffleOptions() { return shuffleOptions; }
    public void setShuffleOptions(boolean shuffleOptions) { this.shuffleOptions = shuffleOptions; }
    public String getSelectionMode() { return selectionMode; }
    public void setSelectionMode(String selectionMode) { this.selectionMode = selectionMode; }
    public Integer getMinSelections() { return minSelections; }
    public void setMinSelections(Integer minSelections) { this.minSelections = minSelections; }
    public Integer getMaxSelections() { return maxSelections; }
    public void setMaxSelections(Integer maxSelections) { this.maxSelections = maxSelections; }
    public String getTextMode() { return textMode; }
    public void setTextMode(String textMode) { this.textMode = textMode; }
    public String getPlaceholder() { return placeholder; }
    public void setPlaceholder(String placeholder) { this.placeholder = placeholder; }
    public Integer getMinLength() { return minLength; }
    public void setMinLength(Integer minLength) { this.minLength = minLength; }
    public Integer getMaxLength() { return maxLength; }
    public void setMaxLength(Integer maxLength) { this.maxLength = maxLength; }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
