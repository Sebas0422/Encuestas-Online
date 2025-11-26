package com.example.encuestas_api.reports.application.port.out;

import com.example.encuestas_api.responses.domain.model.Submission;

import java.util.List;

public interface ListFormSubmissionsPort {
    List<Submission> findByFormId(Long formId);
}
