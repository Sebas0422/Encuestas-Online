package com.example.encuestas_api.questions.application.port.out;

public interface ComputeNextQuestionPositionPort {
    int nextPosition(Long formId, Long sectionId);
}
