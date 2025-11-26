package com.example.encuestas_api.notifications.application.usecase;

import com.example.encuestas_api.notifications.application.dto.EnqueueNotificationCommand;
import com.example.encuestas_api.notifications.application.port.in.EnqueueNotificationUseCase;
import com.example.encuestas_api.notifications.application.port.out.LoadPreferencesPort;
import com.example.encuestas_api.notifications.application.port.out.LoadTemplatePort;
import com.example.encuestas_api.notifications.application.port.out.SaveNotificationPort;
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
public class EnqueueNotificationService implements EnqueueNotificationUseCase {

    private final SaveNotificationPort savePort;
    private final LoadTemplatePort templatePort;
    private final LoadPreferencesPort prefPort;
    private final NotificationFactory factory;

    public EnqueueNotificationService(SaveNotificationPort savePort,
                                      LoadTemplatePort templatePort,
                                      LoadPreferencesPort prefPort,
                                      NotificationFactory factory) {
        this.savePort = savePort;
        this.templatePort = templatePort;
        this.prefPort = prefPort;
        this.factory = factory;
    }

    @Override
    public Notification handle(EnqueueNotificationCommand cmd) {
        NotificationType type = cmd.type();
        TemplateModel model = new TemplateModel(cmd.model());

        NotificationTemplate tpl = templatePort.loadByCode(templateCodeFor(type))
                .orElseThrow(() -> new IllegalArgumentException("No template found for notification type: " + type));
        Map<String, NotificationTemplate> byCode = Map.of(tpl.getCode(), tpl);

        Set<Long> userIds = new HashSet<>();
        cmd.recipients().forEach(r -> { if (r.getUserId() != null) userIds.add(r.getUserId()); });
        Map<Long, NotificationPreference> prefs = prefPort.loadByUserIds(userIds);

        Set<Channel> defaults = (cmd.defaultChannels() == null || cmd.defaultChannels().isEmpty())
                ? EnumSet.of(Channel.EMAIL, Channel.IN_APP)
                : EnumSet.copyOf(cmd.defaultChannels());

        List<Recipient> recips = new ArrayList<>();
        cmd.recipients().forEach(recips::add);

        List<Notification> built = factory.fromEvent(
                type,
                model,
                byCode,
                prefs.values(),
                recips,
                defaults,
                cmd.scheduledAt() == null ? Instant.now() : cmd.scheduledAt()
        );

        Notification firstSaved = null;
        for (Notification n : built) {
            Notification saved = savePort.save(n);
            if (firstSaved == null) firstSaved = saved;
        }
        return firstSaved;
    }

    private String templateCodeFor(NotificationType type) {
        return switch (type) {
            case SUBMISSION_RECEIVED     -> "submission_received";
            case RESPONSE_LIMIT_REACHED  -> "response_limit_reached";
            case FORM_PUBLISHED          -> "form_published";
            case FORM_CLOSED             -> "form_closed";
            case CAMPAIGN_STARTS_TOMORROW-> "campaign_starts_tomorrow";
            case CAMPAIGN_ENDS_TOMORROW  -> "campaign_ends_tomorrow";
            case DAILY_SUMMARY           -> "daily_summary";
        };
    }
}
