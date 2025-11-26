package com.example.encuestas_api.notifications.infrastructure.adapter.scheduler;

import com.example.encuestas_api.notifications.application.port.out.*;
import com.example.encuestas_api.notifications.domain.model.Notification;
import com.example.encuestas_api.notifications.domain.valueobject.DeliveryStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class NotificationsDispatcherJob {

    private static final Logger log = LoggerFactory.getLogger(NotificationsDispatcherJob.class);

    private final FindPendingNotificationsPort findPort;
    private final UpdateNotificationPort updatePort;
    private final SaveNotificationPort savePort;
    private final SendEmailPort emailSender;
    private final SendInAppPort inAppSender;
    private final SendWebhookPort webhookSender;

    public NotificationsDispatcherJob(FindPendingNotificationsPort findPort,
                                      UpdateNotificationPort updatePort,
                                      SaveNotificationPort savePort,
                                      SendEmailPort emailSender,
                                      SendInAppPort inAppSender,
                                      SendWebhookPort webhookSender) {
        this.findPort = findPort;
        this.updatePort = updatePort;
        this.savePort = savePort;
        this.emailSender = emailSender;
        this.inAppSender = inAppSender;
        this.webhookSender = webhookSender;
    }

    @Scheduled(fixedDelay = 30000, initialDelay = 10000)
    public void dispatch() {
        List<Notification> due = findPort.findPending(DeliveryStatus.PENDING, Instant.now(), 100);
        if (due.isEmpty()) return;

        for (Notification n : due) {
            try {
                switch (n.getChannel()) {
                    case EMAIL -> emailSender.send(n.getRecipient(), n.getMessage());
                    case IN_APP -> inAppSender.send(n.getRecipient(), n.getMessage());
                    case WEBHOOK -> webhookSender.send(n.getRecipient(), n.getMessage());
                    default -> throw new IllegalArgumentException("Canal no soportado: " + n.getChannel());
                }
                n.markSent(Instant.now());
                updatePort.update(n);
            } catch (Exception ex) {
                log.warn("Fallo enviando notificación {}: {}", n.getId(), ex.getMessage());
                n.markFailed();
                updatePort.update(n);
            }
        }
    }
}
