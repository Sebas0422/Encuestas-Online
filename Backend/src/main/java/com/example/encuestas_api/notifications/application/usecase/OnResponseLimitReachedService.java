package com.example.encuestas_api.notifications.application.usecase;

import com.example.encuestas_api.notifications.application.port.in.OnResponseLimitReachedUseCase;
import com.example.encuestas_api.notifications.application.port.out.LoadPreferencesPort;
import com.example.encuestas_api.notifications.application.port.out.LoadTemplatePort;
import com.example.encuestas_api.notifications.application.port.out.ResolveRecipientsPort;
import com.example.encuestas_api.notifications.application.port.out.SaveNotificationPort;
import com.example.encuestas_api.notifications.domain.event.ResponseLimitReachedEvent;
import com.example.encuestas_api.notifications.domain.model.NotificationType;
import com.example.encuestas_api.notifications.domain.service.NotificationFactory;
import com.example.encuestas_api.notifications.domain.valueobject.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
@Transactional
public class OnResponseLimitReachedService implements OnResponseLimitReachedUseCase {

    private final ResolveRecipientsPort resolveRecipients;
    private final LoadTemplatePort templatePort;
    private final LoadPreferencesPort prefsPort;
    private final SaveNotificationPort savePort;
    private final NotificationFactory factory;

    public OnResponseLimitReachedService(ResolveRecipientsPort resolveRecipients,
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
    public void handle(ResponseLimitReachedEvent ev) {
        var type = NotificationType.RESPONSE_LIMIT_REACHED;

        Map<String,Object> model = new HashMap<>();
        model.put("form_id", ev.formId());
        model.put("limit_n", ev.limitN());
        model.put("occurred_at", ev.occurredAt().toString());

        var tpl = templatePort.loadByCode("response_limit_reached").orElseGet(() ->
                defaultTemplateFor("response_limit_reached")
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
