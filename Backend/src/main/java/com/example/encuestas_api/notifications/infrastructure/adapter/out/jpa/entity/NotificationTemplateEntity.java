package com.example.encuestas_api.notifications.infrastructure.adapter.out.jpa.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "notification_templates")
public class NotificationTemplateEntity {

    @Id
    @Column(length = 100)
    private String code;

    @Column(length = 300)
    private String subjectTemplate;

    @Lob
    private String bodyTemplate;

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getSubjectTemplate() { return subjectTemplate; }
    public void setSubjectTemplate(String subjectTemplate) { this.subjectTemplate = subjectTemplate; }
    public String getBodyTemplate() { return bodyTemplate; }
    public void setBodyTemplate(String bodyTemplate) { this.bodyTemplate = bodyTemplate; }
}
