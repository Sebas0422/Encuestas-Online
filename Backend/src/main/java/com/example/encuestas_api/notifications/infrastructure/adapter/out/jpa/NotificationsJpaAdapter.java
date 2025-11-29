package com.example.encuestas_api.notifications.infrastructure.adapter.out.jpa;

import com.example.encuestas_api.notifications.application.port.out.FindPendingNotificationsPort;
import com.example.encuestas_api.notifications.application.port.out.SaveNotificationPort;
import com.example.encuestas_api.notifications.application.port.out.UpdateNotificationPort;
import com.example.encuestas_api.notifications.domain.model.Notification;
import com.example.encuestas_api.notifications.domain.valueobject.DeliveryStatus;
import com.example.encuestas_api.notifications.infrastructure.adapter.out.jpa.entity.NotificationEntity;
import com.example.encuestas_api.notifications.infrastructure.adapter.out.jpa.mapper.NotificationsJpaMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Component
@Transactional
public class NotificationsJpaAdapter implements
        SaveNotificationPort,
        FindPendingNotificationsPort,
        UpdateNotificationPort {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Notification save(Notification notification) {
        NotificationEntity e = NotificationsJpaMapper.toEntity(notification);
        if (e.getId() == null) {
            em.persist(e);
        } else {
            e = em.merge(e);
        }
        em.flush();
        return NotificationsJpaMapper.toDomain(e);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notification> findPending(DeliveryStatus status, Instant cutoff, int limit) {
        String st = (status == null ? DeliveryStatus.PENDING.name() : status.name());
        Instant ts = (cutoff == null ? Instant.now() : cutoff);
        int max = Math.max(1, limit);

        var entities = em.createQuery("""
                select n
                from NotificationEntity n
                where n.status = :status
                  and (n.scheduledAt is null or n.scheduledAt <= :cutoff)
                order by n.scheduledAt nulls first, n.id asc
                """, NotificationEntity.class)
                .setParameter("status", st)
                .setParameter("cutoff", ts)
                .setMaxResults(max)
                .getResultList();

        return entities.stream().map(NotificationsJpaMapper::toDomain).toList();
    }

    @Override
    public Notification update(Notification notification) {
        if (notification.getId() == null) {
            throw new IllegalArgumentException("Notification id requerido para update");
        }
        NotificationEntity e = NotificationsJpaMapper.toEntity(notification);
        e = em.merge(e);
        em.flush();
        return NotificationsJpaMapper.toDomain(e);
    }
}
