package com.example.encuestas_api.notifications.domain.event;

import java.time.Instant;

public record SubmissionSubmittedEvent(Long submissionId,
                                       Long formId,
                                       String respondentRepr,
                                       Instant occurredAt) {}
