package com.example.encuestas_api.forms.application.usecase;

import com.example.encuestas_api.forms.application.port.in.GetFormUseCase;
import com.example.encuestas_api.forms.application.port.out.LoadFormPort;
import com.example.encuestas_api.forms.domain.exception.FormNotFoundException;
import com.example.encuestas_api.forms.domain.model.Form;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GetFormService implements GetFormUseCase {
    private final LoadFormPort loadPort;
    public GetFormService(LoadFormPort loadPort){ this.loadPort = loadPort; }
    @Override public Form handle(Long formId){
        return loadPort.loadById(formId).orElseThrow(() -> new FormNotFoundException(formId));
    }
}
