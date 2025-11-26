package com.example.encuestas_api.responses.infrastructure.adapter.out.jpa;

import com.example.encuestas_api.common.dto.PagedResult;
import com.example.encuestas_api.responses.application.port.out.*;
import com.example.encuestas_api.responses.domain.model.Submission;
import com.example.encuestas_api.responses.domain.model.SubmissionStatus;
import com.example.encuestas_api.responses.infrastructure.adapter.out.jpa.entity.SubmissionEntity;
import com.example.encuestas_api.responses.infrastructure.adapter.out.jpa.mapper.ResponsesJpaMapper;
import com.example.encuestas_api.responses.infrastructure.adapter.out.jpa.repository.SubmissionJpaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@Transactional
public class SubmissionRepositoryAdapter implements
        SaveSubmissionPort,
        FindSubmissionPort,
        SearchSubmissionsPort,
        DeleteSubmissionPort,
        ExistsSubmittedByRespondentPort,
        CountSubmittedByFormPort {

    private final SubmissionJpaRepository repo;

    public SubmissionRepositoryAdapter(SubmissionJpaRepository repo) {
        this.repo = repo;
    }

    @Override
    public Submission save(Submission submission) {
        SubmissionEntity e = ResponsesJpaMapper.toEntity(submission);
        var saved = repo.save(e);
        return ResponsesJpaMapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Submission> findById(Long id) {
        return repo.findById(id).map(ResponsesJpaMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResult<Submission> findByForm(Long formId, SubmissionStatus status, int page, int size) {
        var pageable = PageRequest.of(page, size);
        var p = (status == null)
                ? repo.findByFormId(formId, pageable)
                : repo.findByFormIdAndStatus(formId, status.name(), pageable);

        return new PagedResult<>(
                p.map(ResponsesJpaMapper::toDomain).getContent(),
                p.getTotalElements(),
                page,
                size
        );
    }

    @Override
    public void deleteById(Long id) {
        repo.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsSubmittedByFormAndUser(Long formId, Long userId) {
        return repo.existsByFormIdAndStatusAndRespondentUserId(formId, SubmissionStatus.SUBMITTED.name(), userId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsSubmittedByFormAndEmail(Long formId, String email) {
        return repo.existsByFormIdAndStatusAndRespondentEmail(formId, SubmissionStatus.SUBMITTED.name(), email);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsSubmittedByFormAndCode(Long formId, String code) {
        return repo.existsByFormIdAndStatusAndRespondentCode(formId, SubmissionStatus.SUBMITTED.name(), code);
    }

    @Override
    @Transactional(readOnly = true)
    public long countSubmittedByForm(Long formId) {
        return repo.countByFormIdAndStatus(formId, SubmissionStatus.SUBMITTED.name());
    }
}
