package com.example.encuestas_api.notifications.infrastructure.adapter.out.jpa;

import com.example.encuestas_api.notifications.application.port.out.LoadTemplatePort;
import com.example.encuestas_api.notifications.domain.model.NotificationType;
import com.example.encuestas_api.notifications.domain.valueobject.NotificationTemplate;
import com.example.encuestas_api.notifications.infrastructure.adapter.out.jpa.entity.NotificationTemplateEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@Transactional(readOnly = true)
public class NotificationTemplateJpaAdapter implements LoadTemplatePort {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<NotificationTemplate> loadByCode(String code) {
        if (code == null || code.isBlank()) return Optional.empty();
        try {
            NotificationTemplateEntity e = em.createQuery("""
                select t from NotificationTemplateEntity t
                where t.code = :code
            """, NotificationTemplateEntity.class)
                    .setParameter("code", code)
                    .getSingleResult();

            return Optional.of(new NotificationTemplate(
                    e.getCode(),
                    e.getSubjectTemplate(),
                    e.getBodyTemplate()
            ));
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<NotificationTemplate> loadByType(NotificationType type) {
        return loadByCode(mapCode(type));
    }

    private String mapCode(NotificationType type) {
        return switch (type) {
            case FORM_PUBLISHED          -> "form_published";
            case FORM_CLOSED             -> "form_closed";
            case SUBMISSION_RECEIVED     -> "submission_received";
            case RESPONSE_LIMIT_REACHED  -> "response_limit_reached";
            case CAMPAIGN_STARTS_TOMORROW-> "campaign_starts_tomorrow";
            case CAMPAIGN_ENDS_TOMORROW  -> "campaign_ends_tomorrow";
            case DAILY_SUMMARY           -> "daily_summary";
        };
    }
}
