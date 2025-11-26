package com.example.encuestas_api.responses.domain.model;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class ChoiceAnswer extends SubmissionAnswer {

    private final Set<Long> selectedOptionIds = new LinkedHashSet<>();

    public ChoiceAnswer(Long questionId, Integer questionVersion, Collection<Long> selectedOptionIds) {
        super(questionId, questionVersion);
        if (selectedOptionIds != null) this.selectedOptionIds.addAll(selectedOptionIds);
    }

    public Set<Long> getSelectedOptionIds() {
        return Collections.unmodifiableSet(selectedOptionIds);
    }
}
