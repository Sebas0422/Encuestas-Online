package com.example.encuestas_api.forms.application.usecase;

import com.example.encuestas_api.forms.application.port.in.DeleteFormUseCase;
import com.example.encuestas_api.forms.application.port.out.DeleteFormPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DeleteFormService implements DeleteFormUseCase {
    private final DeleteFormPort deletePort;
    public DeleteFormService(DeleteFormPort deletePort){ this.deletePort = deletePort; }
    @Override public void handle(Long formId){ deletePort.deleteById(formId); }
}
