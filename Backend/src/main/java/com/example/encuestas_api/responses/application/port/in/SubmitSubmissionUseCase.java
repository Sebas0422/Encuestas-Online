package com.example.encuestas_api.responses.application.port.in;

import com.example.encuestas_api.responses.application.dto.SubmitSubmissionCommand;
import com.example.encuestas_api.responses.domain.model.Submission;

public interface SubmitSubmissionUseCase {
    Submission handle(SubmitSubmissionCommand cmd);
}
