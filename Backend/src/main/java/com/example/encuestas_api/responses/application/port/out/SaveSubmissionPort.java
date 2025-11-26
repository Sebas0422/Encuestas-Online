package com.example.encuestas_api.responses.application.port.out;

import com.example.encuestas_api.responses.domain.model.Submission;

public interface SaveSubmissionPort {
    Submission save(Submission submission);
}
