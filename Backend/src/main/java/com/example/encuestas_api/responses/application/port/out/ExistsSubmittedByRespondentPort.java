package com.example.encuestas_api.responses.application.port.out;

public interface ExistsSubmittedByRespondentPort {
    boolean existsSubmittedByFormAndUser(Long formId, Long userId);
    boolean existsSubmittedByFormAndEmail(Long formId, String email);
    boolean existsSubmittedByFormAndCode(Long formId, String code);
}
