package com.example.encuestas_api.notifications.infrastructure.adapter.scheduler;

import com.example.encuestas_api.notifications.application.port.out.ResolveRecipientsPort;
import com.example.encuestas_api.notifications.application.port.out.SaveNotificationPort;
import com.example.encuestas_api.notifications.domain.model.Notification;
import com.example.encuestas_api.notifications.domain.model.NotificationType;
import com.example.encuestas_api.notifications.domain.valueobject.Channel;
import com.example.encuestas_api.notifications.domain.valueobject.Message;
import com.example.encuestas_api.notifications.domain.valueobject.Recipient;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Component
@ConditionalOnProperty(name = "notifications.reminder.enabled", havingValue = "true", matchIfMissing = true)
@Transactional
public class CampaignRemindersJob {

    private static final Logger log = LoggerFactory.getLogger(CampaignRemindersJob.class);

    @PersistenceContext
    private EntityManager em;

    private final ResolveRecipientsPort resolver;
    private final SaveNotificationPort save;

    @Value("${notifications.reminder.days_ahead:1}")
    private int daysAhead;

    public CampaignRemindersJob(ResolveRecipientsPort resolver,
                                SaveNotificationPort save) {
        this.resolver = resolver;
        this.save = save;
    }

    @Scheduled(cron = "${notifications.reminder.daily.cron:0 0 9 * * *}")
    public void enqueueDailyCampaignReminders() {
        LocalDate target = LocalDate.now().plusDays(Math.max(1, daysAhead));

        List<Long> starting = em.createQuery("""
                select c.id
                from com.example.encuestas_api.campaigns.infrastructure.adapter.out.jpa.entity.CampaignEntity c
                where c.startDate = :target
                """, Long.class)
                .setParameter("target", target)
                .getResultList();

        List<Long> ending = em.createQuery("""
                select c.id
                from com.example.encuestas_api.campaigns.infrastructure.adapter.out.jpa.entity.CampaignEntity c
                where c.endDate = :target
                """, Long.class)
                .setParameter("target", target)
                .getResultList();

        if (starting.isEmpty() && ending.isEmpty()) {
            log.debug("[Reminders] No hay campañas para recordar (target={}).", target);
            return;
        }

        for (Long campaignId : starting) {
            enqueueForCampaign(campaignId,
                    NotificationType.CAMPAIGN_STARTS_TOMORROW,
                    "La campaña inicia pronto",
                    "La campaña " + campaignId + " inicia en " + daysAhead + " día(s).");
        }

        for (Long campaignId : ending) {
            enqueueForCampaign(campaignId,
                    NotificationType.CAMPAIGN_ENDS_TOMORROW,
                    "La campaña termina pronto",
                    "La campaña " + campaignId + " termina en " + daysAhead + " día(s).");
        }
    }

    private void enqueueForCampaign(Long campaignId,
                                    NotificationType type,
                                    String subject,
                                    String body) {
        List<Recipient> recipients = resolver.resolve(type, null, campaignId, null);
        if (recipients.isEmpty()) {
            log.debug("[Reminders] Campaña {} sin destinatarios ({}).", campaignId, type);
            return;
        }

        Map<String, Object> metadata = Map.of(
                "campaignId", campaignId,
                "type", type.name(),
                "daysAhead", daysAhead
        );

        int created = 0;
        for (Recipient r : recipients) {
            Channel ch = pickChannel(r);
            Notification n = new Notification(
                    type,
                    ch,
                    r,
                    new Message(subject, body),
                    Instant.now(),
                    metadata
            );
            save.save(n);
            created++;
        }
        log.info("[Reminders] Encoladas {} notificaciones para campaña {} ({})", created, campaignId, type);
    }

    private Channel pickChannel(Recipient r) {
        if (r.getEmail() != null && !r.getEmail().isBlank()) return Channel.EMAIL;
        if (r.getWebhook() != null && !r.getWebhook().isBlank()) return Channel.WEBHOOK;
        return Channel.IN_APP;
    }
}
