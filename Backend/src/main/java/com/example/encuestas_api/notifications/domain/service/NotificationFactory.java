package com.example.encuestas_api.notifications.domain.service;

import com.example.encuestas_api.notifications.domain.exception.UnknownTemplateException;
import com.example.encuestas_api.notifications.domain.model.Notification;
import com.example.encuestas_api.notifications.domain.model.NotificationType;
import com.example.encuestas_api.notifications.domain.valueobject.*;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class NotificationFactory {

    public List<Notification> fromEvent(NotificationType type,
                                        TemplateModel model,
                                        Map<String, NotificationTemplate> templatesByCode,
                                        Collection<NotificationPreference> preferences,
                                        Collection<Recipient> candidateRecipients,
                                        Set<Channel> defaultChannels,
                                        Instant now) {

        String tplCode = templateCodeFor(type);
        NotificationTemplate tpl = templatesByCode.get(tplCode);
        if (tpl == null) throw new UnknownTemplateException(tplCode);

        Map<Long, NotificationPreference> prefByUser = preferences.stream()
                .collect(Collectors.toMap(NotificationPreference::getUserId, p -> p, (a,b)->a));

        List<Notification> out = new ArrayList<>();

        for (Recipient r : candidateRecipients) {
            Set<Channel> channels = switch (r.getType()) {
                case USER -> prefByUser.getOrDefault(r.getUserId(),
                                new NotificationPreference(r.getUserId(), Set.of(type), defaultChannels, null))
                        .getChannels()
                        .stream()
                        .filter(ch -> prefByUser.getOrDefault(r.getUserId(),
                                        new NotificationPreference(r.getUserId(), Set.of(type), defaultChannels, null))
                                .allows(type, ch, now))
                        .collect(Collectors.toCollection(() -> EnumSet.noneOf(Channel.class)));

                case EMAIL -> EnumSet.of(Channel.EMAIL);
                case WEBHOOK -> EnumSet.of(Channel.WEBHOOK);
            };

            if (channels.isEmpty()) continue;

            Message msg = NotificationRenderer.render(tpl, model);
            for (Channel ch : channels) {
                out.add(new Notification(type, ch, r, msg, now, Map.of()));
            }
        }
        return out;
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
