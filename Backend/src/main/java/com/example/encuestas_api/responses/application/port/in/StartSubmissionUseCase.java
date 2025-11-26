package com.example.encuestas_api.responses.application.port.in;

import com.example.encuestas_api.responses.application.dto.StartSubmissionCommand;
import com.example.encuestas_api.responses.domain.model.Submission;

public interface StartSubmissionUseCase {
    Submission handle(StartSubmissionCommand cmd);
}
