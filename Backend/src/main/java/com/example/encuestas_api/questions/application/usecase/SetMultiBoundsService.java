package com.example.encuestas_api.questions.application.usecase;

import com.example.encuestas_api.questions.application.port.in.SetMultiBoundsUseCase;
import com.example.encuestas_api.questions.application.port.out.LoadQuestionPort;
import com.example.encuestas_api.questions.application.port.out.SaveQuestionPort;
import com.example.encuestas_api.questions.domain.model.Question;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

@Service
@Transactional
public class SetMultiBoundsService implements SetMultiBoundsUseCase {

    private final LoadQuestionPort loadPort; private final SaveQuestionPort savePort; private final Clock clock;
    public SetMultiBoundsService(LoadQuestionPort loadPort, SaveQuestionPort savePort, Clock clock){
        this.loadPort = loadPort; this.savePort = savePort; this.clock = clock;
    }
    @Override public Question handle(Long questionId, Integer min, Integer max){
        var q = loadPort.loadById(questionId).orElseThrow();
        var updated = q.setMultiBounds(min, max, Instant.now(clock));
        return savePort.save(updated);
    }
}
