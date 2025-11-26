package com.example.encuestas_api.notifications.application.usecase;

import com.example.encuestas_api.notifications.application.dto.DispatchPendingCommand;
import com.example.encuestas_api.notifications.application.port.in.DispatchPendingNotificationsUseCase;
import com.example.encuestas_api.notifications.application.port.out.*;
import com.example.encuestas_api.notifications.domain.model.Notification;
import com.example.encuestas_api.notifications.domain.valueobject.DeliveryStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@Transactional
public class DispatchPendingNotificationsService implements DispatchPendingNotificationsUseCase {

    private final FindPendingNotificationsPort findPort;
    private final UpdateNotificationPort updatePort;
    private final SendEmailPort emailPort;
    private final SendWebhookPort webhookPort;
    private final SendInAppPort inAppPort;

    public DispatchPendingNotificationsService(FindPendingNotificationsPort findPort,
                                               UpdateNotificationPort updatePort,
                                               SendEmailPort emailPort,
                                               SendWebhookPort webhookPort,
                                               SendInAppPort inAppPort) {
        this.findPort = findPort;
        this.updatePort = updatePort;
        this.emailPort = emailPort;
        this.webhookPort = webhookPort;
        this.inAppPort = inAppPort;
    }

    @Override
    public int handle(DispatchPendingCommand cmd) {
        Instant now = cmd.now() == null ? Instant.now() : cmd.now();
        List<Notification> batch = findPort.findPending(DeliveryStatus.PENDING, now, Math.max(1, cmd.maxBatch()));

        for (Notification n : batch) {
            try {
                switch (n.getChannel()) {
                    case EMAIL -> emailPort.send(n.getRecipient(), n.getMessage());
                    case WEBHOOK -> webhookPort.send(n.getRecipient(), n.getMessage());
                    case IN_APP -> inAppPort.send(n.getRecipient(), n.getMessage());
                }
                n.markSent(now);
            } catch (Exception ex) {
                n.markFailed();
            }
            updatePort.update(n);
        }
        return batch.size();
    }
}
