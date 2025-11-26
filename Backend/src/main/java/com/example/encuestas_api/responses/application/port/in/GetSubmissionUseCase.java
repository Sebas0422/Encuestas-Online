package com.example.encuestas_api.responses.application.port.in;

import com.example.encuestas_api.responses.domain.model.Submission;

public interface GetSubmissionUseCase { Submission handle(Long id); }

