package com.example.encuestas_api.responses.domain.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MatchingAnswer extends SubmissionAnswer {

    private final List<MatchingPair> pairs = new ArrayList<>();

    public MatchingAnswer(Long questionId, Integer questionVersion, Collection<MatchingPair> pairs) {
        super(questionId, questionVersion);
        if (pairs != null) this.pairs.addAll(pairs);
    }

    public List<MatchingPair> getPairs() {
        return Collections.unmodifiableList(pairs);
    }
}
