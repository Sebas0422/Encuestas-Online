package com.example.encuestas_api.campaigns.infrastructure.adapter.out.jpa;

import com.example.encuestas_api.campaigns.application.dto.CampaignListQuery;
import com.example.encuestas_api.campaigns.application.port.out.*;
import com.example.encuestas_api.campaigns.domain.model.Campaign;
import com.example.encuestas_api.campaigns.infrastructure.adapter.out.jpa.entity.CampaignEntity;
import com.example.encuestas_api.campaigns.infrastructure.adapter.out.jpa.mapper.CampaignJpaMapper;
import com.example.encuestas_api.campaigns.infrastructure.adapter.out.jpa.repository.CampaignJpaRepository;
import com.example.encuestas_api.campaigns.infrastructure.adapter.out.jpa.repository.CampaignSpecifications;
import com.example.encuestas_api.common.dto.PagedResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CampaignRepositoryAdapter implements
        ExistsCampaignByNamePort, SaveCampaignPort, LoadCampaignPort, DeleteCampaignPort, SearchCampaignsPort {

    private final CampaignJpaRepository jpa;

    public CampaignRepositoryAdapter(CampaignJpaRepository jpa) { this.jpa = jpa; }

    @Override public boolean existsByNameIgnoreCase(String name) { return jpa.existsByNameIgnoreCase(name); }

    @Override public Campaign save(Campaign campaign) {
        CampaignEntity saved = jpa.save(CampaignJpaMapper.toEntity(campaign));
        return CampaignJpaMapper.toDomain(saved);
    }

    @Override public Optional<Campaign> loadById(Long id) {
        return jpa.findById(id).map(CampaignJpaMapper::toDomain);
    }

    @Override public void deleteById(Long id) { jpa.deleteById(id); }

    @Override
    public PagedResult<Campaign> search(CampaignListQuery q, Long userId) {

        Specification<CampaignEntity> spec = Specification
                .where(CampaignSpecifications.searchTerm(q.search()))
                .and(CampaignSpecifications.statusEquals(q.status()))
                .and(CampaignSpecifications.startFrom(q.startFrom()))
                .and(CampaignSpecifications.endTo(q.endTo()))
                .and(CampaignSpecifications.belongsToUser(userId));

        Pageable pageable = PageRequest.of(q.page(), q.size(), Sort.by(Sort.Direction.DESC, "id"));

        Page<CampaignEntity> page = jpa.findAll(spec, pageable);

        return new PagedResult<>(
                page.getContent().stream().map(CampaignJpaMapper::toDomain).toList(),
                page.getTotalElements(),
                page.getNumber(),
                page.getSize()
        );
    }

}
