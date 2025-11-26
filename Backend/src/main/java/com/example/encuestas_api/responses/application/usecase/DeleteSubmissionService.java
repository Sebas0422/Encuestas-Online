package com.example.encuestas_api.responses.application.usecase;

import com.example.encuestas_api.responses.application.port.in.DeleteSubmissionUseCase;
import com.example.encuestas_api.responses.application.port.out.DeleteSubmissionPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DeleteSubmissionService implements DeleteSubmissionUseCase {

    private final DeleteSubmissionPort deletePort;

    public DeleteSubmissionService(DeleteSubmissionPort deletePort) {
        this.deletePort = deletePort;
    }

    @Override
    public void handle(Long id) {
        deletePort.deleteById(id);
    }
}
