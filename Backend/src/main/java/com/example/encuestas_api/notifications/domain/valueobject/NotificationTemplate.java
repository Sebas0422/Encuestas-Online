package com.example.encuestas_api.notifications.domain.valueobject;

import java.util.Objects;

public final class NotificationTemplate {
    private final String code;
    private final String subjectTmpl;
    private final String bodyTmpl;

    public NotificationTemplate(String code, String subjectTmpl, String bodyTmpl) {
        this.code = Objects.requireNonNull(code);
        this.subjectTmpl = subjectTmpl;
        this.bodyTmpl = Objects.requireNonNull(bodyTmpl);
    }

    public String getCode() { return code; }
    public String getSubjectTmpl() { return subjectTmpl; }
    public String getBodyTmpl() { return bodyTmpl; }
}
