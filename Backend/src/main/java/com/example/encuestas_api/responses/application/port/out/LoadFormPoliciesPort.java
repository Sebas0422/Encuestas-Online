package com.example.encuestas_api.responses.application.port.out;

import java.time.Instant;

public interface LoadFormPoliciesPort {

    FormPolicies getPolicies(Long formId);

    enum ResponseLimitMode { UNLIMITED, ONE_PER_RESPONDENT, LIMITED_N }

    record FormPolicies(
            Long formId,
            boolean anonymousAllowed,
            boolean allowEditBeforeSubmit,
            Instant openAt,
            Instant closeAt,
            ResponseLimitMode limitMode,
            Integer limitedN
    ) {}
}
