package com.example.encuestas_api.reports.domain.model;

import com.example.encuestas_api.responses.domain.valueobject.QuestionKind;

import java.util.List;

public class MatchingQuestionReport extends QuestionReport {
    private final List<Long> leftIds;
    private final List<Long> rightIds;
    private final List<MatchingPairStat> pairFrequencies;

    public MatchingQuestionReport(Long questionId,
                                  long answeredCount,
                                  long omittedCount,
                                  List<Long> leftIds,
                                  List<Long> rightIds,
                                  List<MatchingPairStat> pairFrequencies) {
        super(questionId, QuestionKind.MATCHING, answeredCount, omittedCount);
        this.leftIds = leftIds;
        this.rightIds = rightIds;
        this.pairFrequencies = pairFrequencies;
    }

    public List<Long> getLeftIds() { return leftIds; }
    public List<Long> getRightIds() { return rightIds; }
    public List<MatchingPairStat> getPairFrequencies() { return pairFrequencies; }
}
