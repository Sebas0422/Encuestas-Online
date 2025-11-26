package com.example.encuestas_api.responses.application.port.in;

import com.example.encuestas_api.responses.application.dto.SaveChoiceAnswerCommand;
import com.example.encuestas_api.responses.domain.model.Submission;

public interface SaveChoiceAnswerUseCase {
    Submission handle(SaveChoiceAnswerCommand cmd);
}
