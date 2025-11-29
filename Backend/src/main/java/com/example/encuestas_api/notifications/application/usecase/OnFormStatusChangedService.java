package com.example.encuestas_api.notifications.application.usecase;

import com.example.encuestas_api.notifications.application.port.in.OnFormStatusChangedUseCase;
import com.example.encuestas_api.notifications.application.port.out.LoadPreferencesPort;
import com.example.encuestas_api.notifications.application.port.out.LoadTemplatePort;
import com.example.encuestas_api.notifications.application.port.out.ResolveRecipientsPort;
import com.example.encuestas_api.notifications.application.port.out.SaveNotificationPort;
import com.example.encuestas_api.notifications.domain.event.FormStatusChangedEvent;
import com.example.encuestas_api.notifications.domain.model.NotificationType;
import com.example.encuestas_api.notifications.domain.service.NotificationFactory;
import com.example.encuestas_api.notifications.domain.valueobject.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
@Transactional
public class OnFormStatusChangedService implements OnFormStatusChangedUseCase {

    private final ResolveRecipientsPort resolveRecipients;
    private final LoadTemplatePort templatePort;
    private final LoadPreferencesPort prefsPort;
    private final SaveNotificationPort savePort;
    private final NotificationFactory factory;

    public OnFormStatusChangedService(ResolveRecipientsPort resolveRecipients,
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
    public void handle(FormStatusChangedEvent ev) {
        NotificationType type = switch (ev.to()) {
            case published -> NotificationType.FORM_PUBLISHED;
            case closed, archived -> NotificationType.FORM_CLOSED;
            default -> null;
        };
        if (type == null) return;

        Map<String,Object> model = new HashMap<>();
        model.put("form_id", ev.formId());
        model.put("from", ev.from().name());
        model.put("to", ev.to().name());
        model.put("occurred_at", ev.occurredAt().toString());

        String code = (type == NotificationType.FORM_PUBLISHED) ? "form_published" : "form_closed";
        var tpl = templatePort.loadByCode(code).orElseGet(() ->
                defaultTemplateFor(code)
        );
        var byCode = Map.of(tpl.getCode(), tpl);

        var recips = resolveRecipients.resolve(type, ev.formId(), null, null);

        Set<Long> userIds = new HashSet<>();
        recips.forEach(r -> { if (r.getUserId() != null) userIds.add(r.getUserId()); });
        var prefs = prefsPort.loadByUserIds(userIds);

        var defaults = EnumSet.of(Channel.EMAIL, Channel.IN_APP);
        var built = factory.fromEvent(type, new TemplateModel(model), byCode, prefs.values(), recips, defaults, Instant.now());
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
