package com.example.encuestas_api.notifications.application.port.out;

import com.example.encuestas_api.notifications.domain.model.NotificationType;
import com.example.encuestas_api.notifications.domain.valueobject.Recipient;

import java.util.List;

public interface ResolveRecipientsPort {
    List<Recipient> resolve(NotificationType type, Long formId, Long campaignId, Long submissionId);
}
