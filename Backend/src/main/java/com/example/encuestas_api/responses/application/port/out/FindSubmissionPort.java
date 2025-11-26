package com.example.encuestas_api.responses.application.port.out;

import com.example.encuestas_api.responses.domain.model.Submission;

import java.util.Optional;

public interface FindSubmissionPort {
    Optional<Submission> findById(Long id);
}
