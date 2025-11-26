package com.example.encuestas_api.notifications.infrastructure.adapter.out.sender;

import com.example.encuestas_api.notifications.application.port.out.SendWebhookPort;
import com.example.encuestas_api.notifications.domain.valueobject.Message;
import com.example.encuestas_api.notifications.domain.valueobject.Recipient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class WebhookSenderAdapter implements SendWebhookPort {

    private final RestTemplate http = new RestTemplate();

    @Override
    public void send(Recipient recipientWebhook, Message message) throws Exception {
        if (recipientWebhook == null || recipientWebhook.getWebhook() == null) {
            throw new IllegalArgumentException("Webhook URL requerido");
        }
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        var body = Map.of("subject", message.getSubject(), "content", message.getBody());
        http.postForEntity(recipientWebhook.getWebhook(), new HttpEntity<>(body, h), Void.class);
    }
}
