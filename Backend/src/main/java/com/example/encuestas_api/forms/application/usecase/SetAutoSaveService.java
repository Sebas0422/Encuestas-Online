package com.example.encuestas_api.forms.application.usecase;

import com.example.encuestas_api.forms.application.port.in.SetAutoSaveUseCase;
import com.example.encuestas_api.forms.application.port.out.LoadFormPort;
import com.example.encuestas_api.forms.application.port.out.SaveFormPort;
import com.example.encuestas_api.forms.domain.exception.FormNotFoundException;
import com.example.encuestas_api.forms.domain.model.Form;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

@Service
@Transactional
public class SetAutoSaveService implements SetAutoSaveUseCase {
    private final LoadFormPort loadPort; private final SaveFormPort savePort; private final Clock clock;
    public SetAutoSaveService(LoadFormPort loadPort, SaveFormPort savePort, Clock clock){
        this.loadPort = loadPort; this.savePort = savePort; this.clock = clock;
    }
    @Override public Form handle(Long formId, boolean enabled){
        var form = loadPort.loadById(formId).orElseThrow(() -> new FormNotFoundException(formId));
        var updated = form.setAutoSave(enabled, Instant.now(clock));
        return savePort.save(updated);
    }
}
