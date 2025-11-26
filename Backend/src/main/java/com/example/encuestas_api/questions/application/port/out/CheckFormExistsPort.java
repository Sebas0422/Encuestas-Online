package com.example.encuestas_api.questions.application.port.out;

public interface CheckFormExistsPort {
    boolean exists(Long formId);
}
