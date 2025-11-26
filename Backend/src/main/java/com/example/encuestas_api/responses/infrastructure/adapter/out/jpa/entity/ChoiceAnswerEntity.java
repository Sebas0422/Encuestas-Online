package com.example.encuestas_api.responses.infrastructure.adapter.out.jpa.entity;

import jakarta.persistence.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "submission_choice_answers")
public class ChoiceAnswerEntity extends SubmissionAnswerEntity {

    @ElementCollection
    @CollectionTable(name = "submission_choice_selected",
            joinColumns = @JoinColumn(name = "answer_id"))
    @Column(name = "option_id", nullable = false)
    private Set<Long> selectedOptionIds = new LinkedHashSet<>();

    public Set<Long> getSelectedOptionIds() { return selectedOptionIds; }
    public void setSelectedOptionIds(Set<Long> selectedOptionIds) { this.selectedOptionIds = selectedOptionIds; }
}
