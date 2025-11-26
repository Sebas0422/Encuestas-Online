package com.example.encuestas_api.forms.application.port.in;
public interface GeneratePublicLinkUseCase {
    String handle(Long formId, boolean force);
}
