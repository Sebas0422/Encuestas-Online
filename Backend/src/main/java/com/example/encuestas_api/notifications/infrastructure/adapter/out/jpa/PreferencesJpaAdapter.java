package com.example.encuestas_api.notifications.infrastructure.adapter.out.jpa;

import com.example.encuestas_api.notifications.application.port.out.LoadPreferencesPort;
import com.example.encuestas_api.notifications.domain.valueobject.NotificationPreference;
import com.example.encuestas_api.notifications.infrastructure.adapter.out.jpa.entity.NotificationPreferenceEntity;
import com.example.encuestas_api.notifications.infrastructure.adapter.out.jpa.mapper.PreferencesJpaMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
@Transactional(readOnly = true)
public class PreferencesJpaAdapter implements LoadPreferencesPort {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Map<Long, NotificationPreference> loadByUserIds(Set<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) return Collections.emptyMap();

        var list = em.createQuery("""
                select p
                from com.example.encuestas_api.notifications.infrastructure.adapter.out.jpa.entity.NotificationPreferenceEntity p
                where p.userId in :ids
                """, NotificationPreferenceEntity.class)
                .setParameter("ids", userIds)
                .getResultList();

        Map<Long, NotificationPreference> out = new HashMap<>(list.size());
        for (var e : list) {
            out.put(e.getUserId(), PreferencesJpaMapper.toDomain(e));
        }
        return out;
    }
}
