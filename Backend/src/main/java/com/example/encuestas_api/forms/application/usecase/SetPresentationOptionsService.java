package com.example.encuestas_api.forms.application.usecase;

import com.example.encuestas_api.forms.application.port.in.SetPresentationOptionsUseCase;
import com.example.encuestas_api.forms.application.port.out.LoadFormPort;
import com.example.encuestas_api.forms.application.port.out.SaveFormPort;
import com.example.encuestas_api.forms.domain.exception.FormNotFoundException;
import com.example.encuestas_api.forms.domain.model.Form;
import com.example.encuestas_api.forms.domain.valueobject.PresentationOptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

@Service
@Transactional
public class SetPresentationOptionsService implements SetPresentationOptionsUseCase {
    private final LoadFormPort loadPort; private final SaveFormPort savePort; private final Clock clock;
    public SetPresentationOptionsService(LoadFormPort loadPort, SaveFormPort savePort, Clock clock){
        this.loadPort = loadPort; this.savePort = savePort; this.clock = clock;
    }
    @Override public Form handle(Long formId, boolean shuffleQuestions, boolean shuffleOptions, boolean progressBar, boolean paginated){
        var form = loadPort.loadById(formId).orElseThrow(() -> new FormNotFoundException(formId));
        var updated = form.setPresentation(PresentationOptions.of(shuffleQuestions, shuffleOptions, progressBar, paginated), Instant.now(clock));
        return savePort.save(updated);
    }
}
