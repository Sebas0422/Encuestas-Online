package com.example.encuestas_api.questions.application.usecase;

import com.example.encuestas_api.questions.application.port.in.RenameQuestionUseCase;
import com.example.encuestas_api.questions.application.port.out.LoadQuestionPort;
import com.example.encuestas_api.questions.application.port.out.SaveQuestionPort;
import com.example.encuestas_api.questions.domain.model.Question;
import com.example.encuestas_api.questions.domain.valueobject.Prompt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

@Service
@Transactional
public class RenameQuestionService implements RenameQuestionUseCase {

    private final LoadQuestionPort loadPort; private final SaveQuestionPort savePort; private final Clock clock;
    public RenameQuestionService(LoadQuestionPort loadPort, SaveQuestionPort savePort, Clock clock){
        this.loadPort = loadPort; this.savePort = savePort; this.clock = clock;
    }
    @Override public Question handle(Long questionId, String newPrompt){
        var q = loadPort.loadById(questionId).orElseThrow();
        var updated = q.rename(Prompt.of(newPrompt), Instant.now(clock));
        return savePort.save(updated);
    }
}
