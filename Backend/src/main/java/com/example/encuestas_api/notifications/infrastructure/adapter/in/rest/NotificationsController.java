package com.example.encuestas_api.notifications.infrastructure.adapter.in.rest;

import com.example.encuestas_api.notifications.application.port.out.SaveNotificationPort;
import com.example.encuestas_api.notifications.domain.model.Notification;
import com.example.encuestas_api.notifications.domain.model.NotificationType;
import com.example.encuestas_api.notifications.domain.valueobject.*;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationsController {

    private final SaveNotificationPort savePort;

    public NotificationsController(SaveNotificationPort savePort) {
        this.savePort = savePort;
    }

    private NotificationType def(NotificationType t) {
        return t == null ? NotificationType.DAILY_SUMMARY : t;
    }

    @PostMapping("/email")
    public ResponseEntity<Long> enqueueEmail(@RequestParam @NotBlank String to,
                                             @RequestParam(required = false) String subject,
                                             @RequestParam String body,
                                             @RequestParam(required = false) NotificationType type) {
        var n = new Notification(
                def(type),
                Channel.EMAIL,
                Recipient.email(to),
                new Message(subject == null ? "Notificación" : subject, body),
                Instant.now(),
                Map.of("src", "api-email")
        );
        n = savePort.save(n);
        return ResponseEntity.ok(n.getId());
    }

    @PostMapping("/in-app")
    public ResponseEntity<Long> enqueueInApp(@RequestParam Long userId,
                                             @RequestParam(required = false) String subject,
                                             @RequestParam String body,
                                             @RequestParam(required = false) NotificationType type) {
        var n = new Notification(
                def(type),
                Channel.IN_APP,
                Recipient.user(userId),
                new Message(subject == null ? "Notificación" : subject, body),
                Instant.now(),
                Map.of("src", "api-inapp")
        );
        n = savePort.save(n);
        return ResponseEntity.ok(n.getId());
    }

    @PostMapping("/webhook")
    public ResponseEntity<Long> enqueueWebhook(@RequestParam String url,
                                               @RequestParam(required = false) String subject,
                                               @RequestParam String body,
                                               @RequestParam(required = false) NotificationType type) {
        var n = new Notification(
                def(type),
                Channel.WEBHOOK,
                Recipient.webhook(url),
                new Message(subject == null ? "Notificación" : subject, body),
                Instant.now(),
                Map.of("src", "api-webhook")
        );
        n = savePort.save(n);
        return ResponseEntity.ok(n.getId());
    }
}
