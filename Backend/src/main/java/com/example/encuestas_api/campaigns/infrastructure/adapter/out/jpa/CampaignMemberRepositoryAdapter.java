package com.example.encuestas_api.campaigns.infrastructure.adapter.out.jpa;

import com.example.encuestas_api.campaigns.application.dto.MemberListQuery;
import com.example.encuestas_api.campaigns.application.port.out.*;
import com.example.encuestas_api.campaigns.domain.model.CampaignMember;
import com.example.encuestas_api.campaigns.infrastructure.adapter.out.jpa.entity.CampaignEntity;
import com.example.encuestas_api.campaigns.infrastructure.adapter.out.jpa.entity.CampaignMemberEntity;
import com.example.encuestas_api.campaigns.infrastructure.adapter.out.jpa.mapper.CampaignMemberJpaMapper;
import com.example.encuestas_api.campaigns.infrastructure.adapter.out.jpa.repository.CampaignJpaRepository;
import com.example.encuestas_api.campaigns.infrastructure.adapter.out.jpa.repository.CampaignMemberJpaRepository;
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
public class CampaignMemberRepositoryAdapter implements
        SaveCampaignMemberPort, LoadCampaignMemberPort, DeleteCampaignMemberPort, SearchCampaignMembersPort {

    private final CampaignMemberJpaRepository memberRepo;
    private final CampaignJpaRepository campaignRepo;

    public CampaignMemberRepositoryAdapter(CampaignMemberJpaRepository memberRepo, CampaignJpaRepository campaignRepo) {
        this.memberRepo = memberRepo;
        this.campaignRepo = campaignRepo;
    }

    @Override
    public CampaignMember save(CampaignMember member) {
        CampaignEntity campaign = campaignRepo.getReferenceById(member.getCampaignId());
        CampaignMemberEntity saved = memberRepo.save(
                CampaignMemberJpaMapper.toEntity(member, campaign)
        );
        return CampaignMemberJpaMapper.toDomain(saved);
    }

    @Override
    public Optional<CampaignMember> loadByCampaignIdAndUserId(Long campaignId, Long userId) {
        return memberRepo.findByCampaign_IdAndUserId(campaignId, userId).map(CampaignMemberJpaMapper::toDomain);
    }

    @Override
    public void deleteByCampaignIdAndUserId(Long campaignId, Long userId) {
        memberRepo.deleteByCampaign_IdAndUserId(campaignId, userId);
    }

    @Override
    public PagedResult<CampaignMember> search(MemberListQuery q) {
        Specification<CampaignMemberEntity> spec = Specification
                .where(CampaignSpecifications.memberByCampaign(q.campaignId()))
                .and(CampaignSpecifications.memberRole(q.role()));

        Pageable pageable = PageRequest.of(q.page(), q.size(), Sort.by(Sort.Direction.ASC, "id"));
        Page<CampaignMemberEntity> page = memberRepo.findAll(spec, pageable);

        var items = page.getContent().stream().map(CampaignMemberJpaMapper::toDomain).toList();
        return new PagedResult<>(items, page.getTotalElements(), page.getNumber(), page.getSize());
    }
}
