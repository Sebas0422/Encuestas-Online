package com.example.encuestas_api.reports.application.port.out;

import com.example.encuestas_api.responses.domain.valueobject.QuestionSnapshot;

import java.util.Map;

public interface QuestionsSnapshotPort {
    Map<Long, QuestionSnapshot> byFormId(Long formId);
}
