package com.example.encuestas_api.notifications.infrastructure.adapter.out.resolver;

import com.example.encuestas_api.notifications.application.port.out.ResolveRecipientsPort;
import com.example.encuestas_api.notifications.domain.model.NotificationType;
import com.example.encuestas_api.notifications.domain.valueobject.Recipient;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@Transactional(readOnly = true)
public class RecipientsResolverJpaAdapter implements ResolveRecipientsPort {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Recipient> resolve(NotificationType type, Long formId, Long campaignId, Long submissionId) {
        List<Recipient> out = new ArrayList<>();

        // 1) Determinar campaignId si vino un formId
        Long cid = campaignId;
        if (cid == null && formId != null) {
            // Ajusta según tu entidad de formulario
            // Si tu FormEntity tiene campo 'campaignId' (Long):
            try {
                cid = em.createQuery("""
                        select f.campaignId
                        from com.example.encuestas_api.forms.infrastructure.adapter.out.jpa.entity.FormEntity f
                        where f.id = :fid
                    """, Long.class)
                        .setParameter("fid", formId)
                        .getSingleResult();
            } catch (Exception ignored) {
                // Alternativa si el form mapea la relación:
                cid = em.createQuery("""
                        select f.campaign.id
                        from com.example.encuestas_api.forms.infrastructure.adapter.out.jpa.entity.FormEntity f
                        where f.id = :fid
                    """, Long.class)
                        .setParameter("fid", formId)
                        .getSingleResult();
            }
        }
        if (cid == null) return out;

        // 2) Recuperar miembros de la campaña
        List<Long> userIds;
        try {
            // Opción A: si CampaignMemberEntity tiene @ManyToOne Campaign campaign
            userIds = em.createQuery("""
                    select m.userId
                    from com.example.encuestas_api.campaigns.infrastructure.adapter.out.jpa.entity.CampaignMemberEntity m
                    where m.campaign.id = :cid
                """, Long.class)
                    .setParameter("cid", cid)
                    .getResultList();
        } catch (Exception ex) {
            // Opción B: si CampaignMemberEntity tiene campo campaignId (Long)
            userIds = em.createQuery("""
                    select m.userId
                    from com.example.encuestas_api.campaigns.infrastructure.adapter.out.jpa.entity.CampaignMemberEntity m
                    where m.campaignId = :cid
                """, Long.class)
                    .setParameter("cid", cid)
                    .getResultList();
        }

        for (Long uid : userIds) {
            out.add(Recipient.user(uid));
        }
        return out;
    }
}
