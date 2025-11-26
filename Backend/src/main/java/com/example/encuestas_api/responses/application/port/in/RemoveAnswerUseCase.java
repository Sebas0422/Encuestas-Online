package com.example.encuestas_api.responses.application.port.in;

import com.example.encuestas_api.responses.domain.model.Submission;

public interface RemoveAnswerUseCase {
    Submission handle(Long submissionId, Long questionId);
}
