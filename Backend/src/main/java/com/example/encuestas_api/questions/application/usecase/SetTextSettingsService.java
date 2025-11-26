package com.example.encuestas_api.questions.application.usecase;

import com.example.encuestas_api.questions.application.port.in.SetTextSettingsUseCase;
import com.example.encuestas_api.questions.application.port.out.LoadQuestionPort;
import com.example.encuestas_api.questions.application.port.out.SaveQuestionPort;
import com.example.encuestas_api.questions.domain.model.Question;
import com.example.encuestas_api.questions.domain.model.TextMode;
import com.example.encuestas_api.questions.domain.model.TextSettings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

@Service
@Transactional
public class SetTextSettingsService implements SetTextSettingsUseCase {

    private final LoadQuestionPort loadPort; private final SaveQuestionPort savePort; private final Clock clock;
    public SetTextSettingsService(LoadQuestionPort loadPort, SaveQuestionPort savePort, Clock clock){
        this.loadPort = loadPort; this.savePort = savePort; this.clock = clock;
    }
    @Override public Question handle(Long questionId, String textMode, String placeholder, Integer minLength, Integer maxLength){
        var q = loadPort.loadById(questionId).orElseThrow();
        var mode = (textMode == null ? TextMode.SHORT : TextMode.valueOf(textMode));
        var updated = q.setTextSettings(TextSettings.of(mode, placeholder, minLength, maxLength), Instant.now(clock));
        return savePort.save(updated);
    }
}
