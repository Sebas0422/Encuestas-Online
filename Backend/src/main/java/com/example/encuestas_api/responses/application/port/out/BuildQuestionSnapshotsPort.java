package com.example.encuestas_api.responses.application.port.out;

import com.example.encuestas_api.responses.domain.valueobject.QuestionSnapshot;

import java.util.Map;

public interface BuildQuestionSnapshotsPort {
    Map<Long, QuestionSnapshot> byFormId(Long formId);
}
