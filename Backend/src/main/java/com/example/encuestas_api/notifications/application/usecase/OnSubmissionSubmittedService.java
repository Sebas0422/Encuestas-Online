package com.example.encuestas_api.notifications.application.usecase;

import com.example.encuestas_api.notifications.application.port.in.OnSubmissionSubmittedUseCase;
import com.example.encuestas_api.notifications.application.port.out.LoadPreferencesPort;
import com.example.encuestas_api.notifications.application.port.out.LoadTemplatePort;
import com.example.encuestas_api.notifications.application.port.out.ResolveRecipientsPort;
import com.example.encuestas_api.notifications.application.port.out.SaveNotificationPort;
import com.example.encuestas_api.notifications.domain.event.SubmissionSubmittedEvent;
import com.example.encuestas_api.notifications.domain.model.Notification;
import com.example.encuestas_api.notifications.domain.model.NotificationType;
import com.example.encuestas_api.notifications.domain.service.NotificationFactory;
import com.example.encuestas_api.notifications.domain.valueobject.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
@Transactional
public class OnSubmissionSubmittedService implements OnSubmissionSubmittedUseCase {

    private final ResolveRecipientsPort resolveRecipients;
    private final LoadTemplatePort templatePort;
    private final LoadPreferencesPort prefsPort;
    private final SaveNotificationPort savePort;
    private final NotificationFactory factory;

    public OnSubmissionSubmittedService(ResolveRecipientsPort resolveRecipients,
                                        LoadTemplatePort templatePort,
                                        LoadPreferencesPort prefsPort,
                                        SaveNotificationPort savePort,
                                        NotificationFactory factory) {
        this.resolveRecipients = resolveRecipients;
        this.templatePort = templatePort;
        this.prefsPort = prefsPort;
        this.savePort = savePort;
        this.factory = factory;
    }

    @Override
    public void handle(SubmissionSubmittedEvent ev) {
        NotificationType type = NotificationType.SUBMISSION_RECEIVED;

        Map<String,Object> model = new HashMap<>();
        model.put("submission_id", ev.submissionId());
        model.put("form_id", ev.formId());
        model.put("respondent", ev.respondentRepr());
        model.put("occurred_at", ev.occurredAt().toString());

        TemplateModel tmplModel = new TemplateModel(model);
        NotificationTemplate tpl = templatePort.loadByCode("submission_received").orElseGet(() ->
                defaultTemplateFor("submission_received")
        );
        Map<String, NotificationTemplate> byCode = Map.of(tpl.getCode(), tpl);

        List<Recipient> recips = resolveRecipients.resolve(type, ev.formId(), null, ev.submissionId());

        Set<Long> userIds = new HashSet<>();
        recips.forEach(r -> { if (r.getUserId() != null) userIds.add(r.getUserId()); });
        var prefs = prefsPort.loadByUserIds(userIds);

        var defaults = EnumSet.of(Channel.EMAIL, Channel.IN_APP);
        List<Notification> built = factory.fromEvent(type, tmplModel, byCode, prefs.values(), recips, defaults, Instant.now());

        built.forEach(savePort::save);
    }

    private NotificationTemplate defaultTemplateFor(String code) {
        return switch (code) {
            case "form_published" -> new NotificationTemplate(
                    "form_published",
                    "Formulario {{form_id}} publicado",
                    "El formulario {{form_id}} cambió de {{from}} a {{to}} el {{occurred_at}}."
            );
            case "form_closed" -> new NotificationTemplate(
                    "form_closed",
                    "Formulario {{form_id}} cerrado",
                    "El formulario {{form_id}} cambió de {{from}} a {{to}} el {{occurred_at}}."
            );
            default -> new NotificationTemplate(
                    code,
                    "Notificación",
                    "Evento del sistema para recurso {{form_id}}."
            );
        };
    }
}
