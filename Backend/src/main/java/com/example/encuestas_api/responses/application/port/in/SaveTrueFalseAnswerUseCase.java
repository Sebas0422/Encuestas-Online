package com.example.encuestas_api.responses.application.port.in;

import com.example.encuestas_api.responses.application.dto.SaveTrueFalseAnswerCommand;
import com.example.encuestas_api.responses.domain.model.Submission;

public interface SaveTrueFalseAnswerUseCase {
    Submission handle(SaveTrueFalseAnswerCommand cmd);
}
