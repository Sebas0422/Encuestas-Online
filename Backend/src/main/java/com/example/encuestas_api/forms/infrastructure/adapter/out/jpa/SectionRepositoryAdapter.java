package com.example.encuestas_api.forms.infrastructure.adapter.out.jpa;

import com.example.encuestas_api.common.dto.PagedResult;
import com.example.encuestas_api.forms.application.dto.SectionListQuery;
import com.example.encuestas_api.forms.application.port.out.*;
import com.example.encuestas_api.forms.domain.model.Section;
import com.example.encuestas_api.forms.infrastructure.adapter.out.jpa.entity.SectionEntity;
import com.example.encuestas_api.forms.infrastructure.adapter.out.jpa.mapper.SectionJpaMapper;
import com.example.encuestas_api.forms.infrastructure.adapter.out.jpa.repository.SectionJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
public class SectionRepositoryAdapter implements
        SaveSectionPort, LoadSectionPort, DeleteSectionPort, SearchSectionsPort,
        ComputeNextSectionPositionPort, ReorderSectionsPort {

    private final SectionJpaRepository repo;

    public SectionRepositoryAdapter(SectionJpaRepository repo) { this.repo = repo; }

    @Override
    public Section save(Section section) {
        SectionEntity saved = repo.save(SectionJpaMapper.toEntity(section));
        return SectionJpaMapper.toDomain(saved);
    }

    @Override
    public Optional<Section> loadById(Long sectionId) {
        return repo.findById(sectionId).map(SectionJpaMapper::toDomain);
    }

    @Override
    public void delete(Long formId, Long sectionId) {
        repo.deleteByFormIdAndId(formId, sectionId);
    }

    @Override
    public PagedResult<Section> search(SectionListQuery query) {
        List<SectionEntity> all = repo.findByFormIdOrderByPositionAsc(query.formId());
        int from = Math.min(query.page() * query.size(), all.size());
        int to = Math.min(from + query.size(), all.size());
        var pageItems = all.subList(from, to).stream().map(SectionJpaMapper::toDomain).toList();
        return new PagedResult<>(pageItems, all.size(), query.page(), query.size());
    }

    @Override
    public int nextPositionForForm(Long formId) {
        return repo.countByFormId(formId);
    }

    @Override
    @Transactional
    public Section moveTo(Long formId, Long sectionId, int newPosition) {
        List<SectionEntity> sections = repo.findByFormIdOrderByPositionAsc(formId);
        int currentIdx = -1;
        for (int i = 0; i < sections.size(); i++) {
            if (sections.get(i).getId().equals(sectionId)) { currentIdx = i; break; }
        }
        if (currentIdx < 0) throw new IllegalArgumentException("Sección no encontrada");
        var moving = sections.remove(currentIdx);
        int target = Math.min(Math.max(newPosition, 0), sections.size());
        sections.add(target, moving);

        for (int i = 0; i < sections.size(); i++) {
            sections.get(i).setPosition(i);
        }
        moving.setPosition(target);
        repo.saveAll(sections);
        return SectionJpaMapper.toDomain(repo.save(moving));
    }
}
