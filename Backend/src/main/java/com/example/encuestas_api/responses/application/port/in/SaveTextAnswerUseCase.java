package com.example.encuestas_api.responses.application.port.in;

import com.example.encuestas_api.responses.application.dto.SaveTextAnswerCommand;
import com.example.encuestas_api.responses.domain.model.Submission;

public interface SaveTextAnswerUseCase {
    Submission handle(SaveTextAnswerCommand cmd);
}
