package com.example.encuestas_api.forms.infrastructure.adapter.out.jpa;

import com.example.encuestas_api.common.dto.PagedResult;
import com.example.encuestas_api.forms.application.dto.FormListQuery;
import com.example.encuestas_api.forms.application.port.out.*;
import com.example.encuestas_api.forms.domain.model.Form;
import com.example.encuestas_api.forms.infrastructure.adapter.out.jpa.entity.FormEntity;
import com.example.encuestas_api.forms.infrastructure.adapter.out.jpa.mapper.FormJpaMapper;
import com.example.encuestas_api.forms.infrastructure.adapter.out.jpa.repository.FormJpaRepository;
import com.example.encuestas_api.forms.infrastructure.adapter.out.jpa.repository.FormSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class FormRepositoryAdapter implements
        ExistsFormByTitleInCampaignPort, SaveFormPort, LoadFormPort, DeleteFormPort, SearchFormsPort {

    private final FormJpaRepository repo;

    public FormRepositoryAdapter(FormJpaRepository repo) { this.repo = repo; }

    @Override
    public boolean exists(Long campaignId, String title) {
        return repo.existsByCampaignIdAndTitleIgnoreCase(campaignId, title);
    }

    @Override
    public Form save(Form form) {
        FormEntity saved = repo.save(FormJpaMapper.toEntity(form));
        return FormJpaMapper.toDomain(saved);
    }

    @Override
    public Optional<Form> loadById(Long formId) {
        return repo.findById(formId).map(FormJpaMapper::toDomain);
    }

    @Override
    public void deleteById(Long formId) {
        repo.deleteById(formId);
    }

    @Override
    public PagedResult<Form> search(FormListQuery q) {
        Specification<FormEntity> spec = Specification
                .where(FormSpecifications.campaignId(q.campaignId()))
                .and(FormSpecifications.searchTerm(q.search()))
                .and(FormSpecifications.status(q.status()))
                .and(FormSpecifications.accessMode(q.accessMode()))
                .and(FormSpecifications.openFrom(q.openFrom()))
                .and(FormSpecifications.closeTo(q.closeTo()));

        Pageable pageable = PageRequest.of(q.page(), q.size(), Sort.by(Sort.Direction.DESC, "id"));
        Page<FormEntity> page = repo.findAll(spec, pageable);

        var items = page.getContent().stream().map(FormJpaMapper::toDomain).toList();
        return new PagedResult<>(items, page.getTotalElements(), page.getNumber(), page.getSize());
    }
}
