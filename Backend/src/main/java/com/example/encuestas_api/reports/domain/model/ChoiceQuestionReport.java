package com.example.encuestas_api.reports.domain.model;

import com.example.encuestas_api.responses.domain.valueobject.QuestionKind;
import com.example.encuestas_api.responses.domain.valueobject.SelectionMode;

import java.util.List;

public class ChoiceQuestionReport extends QuestionReport {
    private final SelectionMode selectionMode;
    private final Integer minSelections;
    private final Integer maxSelections;
    private final List<ChoiceOptionStat> options;

    public ChoiceQuestionReport(Long questionId,
                                long answeredCount,
                                long omittedCount,
                                SelectionMode selectionMode,
                                Integer minSelections,
                                Integer maxSelections,
                                List<ChoiceOptionStat> options) {
        super(questionId, QuestionKind.CHOICE, answeredCount, omittedCount);
        this.selectionMode = selectionMode;
        this.minSelections = minSelections;
        this.maxSelections = maxSelections;
        this.options = options;
    }

    public SelectionMode getSelectionMode() { return selectionMode; }
    public Integer getMinSelections() { return minSelections; }
    public Integer getMaxSelections() { return maxSelections; }
    public List<ChoiceOptionStat> getOptions() { return options; }
}
