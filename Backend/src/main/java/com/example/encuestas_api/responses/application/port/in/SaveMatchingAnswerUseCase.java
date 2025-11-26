package com.example.encuestas_api.responses.application.port.in;

import com.example.encuestas_api.responses.application.dto.SaveMatchingAnswerCommand;
import com.example.encuestas_api.responses.domain.model.Submission;

public interface SaveMatchingAnswerUseCase {
    Submission handle(SaveMatchingAnswerCommand cmd);
}
