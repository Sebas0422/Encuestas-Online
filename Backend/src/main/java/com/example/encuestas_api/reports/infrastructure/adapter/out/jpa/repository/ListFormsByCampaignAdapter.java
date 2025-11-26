package com.example.encuestas_api.reports.infrastructure.adapter.out.jpa.repository;

import com.example.encuestas_api.reports.application.port.out.ListFormsByCampaignPort;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Transactional(readOnly = true)
public class ListFormsByCampaignAdapter implements ListFormsByCampaignPort {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Long> findFormIdsByCampaign(Long campaignId) {
        return em.createQuery("""
                select f.id
                from com.example.encuestas_api.forms.infrastructure.adapter.out.jpa.entity.FormEntity f
                where f.campaignId = :cid
                order by f.id asc
                """, Long.class)
                .setParameter("cid", campaignId)
                .getResultList();
    }
}
