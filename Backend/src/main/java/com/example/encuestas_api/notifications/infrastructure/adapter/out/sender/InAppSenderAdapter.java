package com.example.encuestas_api.notifications.infrastructure.adapter.out.sender;

import com.example.encuestas_api.notifications.application.port.out.SendInAppPort;
import com.example.encuestas_api.notifications.domain.valueobject.Message;
import com.example.encuestas_api.notifications.domain.valueobject.Recipient;
import com.example.encuestas_api.notifications.infrastructure.adapter.out.jpa.entity.InAppInboxEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
@Transactional
public class InAppSenderAdapter implements SendInAppPort {

    @PersistenceContext
    private EntityManager em;

    @Override
    public void send(Recipient recipientUser, Message message) {
        if (recipientUser == null || recipientUser.getUserId() == null) {
            throw new IllegalArgumentException("UserId requerido para in-app");
        }
        InAppInboxEntity e = new InAppInboxEntity();
        e.setUserId(recipientUser.getUserId());
        e.setSubject(message.getSubject());
        e.setContent(message.getBody());
        e.setCreatedAt(Instant.now());
        em.persist(e);
    }
}
