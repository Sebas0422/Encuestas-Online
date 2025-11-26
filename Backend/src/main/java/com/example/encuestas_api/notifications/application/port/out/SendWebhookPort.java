package com.example.encuestas_api.notifications.application.port.out;

import com.example.encuestas_api.notifications.domain.valueobject.Message;
import com.example.encuestas_api.notifications.domain.valueobject.Recipient;

public interface SendWebhookPort {
    void send(Recipient recipientWebhook, Message message) throws Exception;
}
