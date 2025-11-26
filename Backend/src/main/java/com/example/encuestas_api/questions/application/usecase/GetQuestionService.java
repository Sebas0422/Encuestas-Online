package com.example.encuestas_api.questions.application.usecase;

import com.example.encuestas_api.questions.application.port.in.GetQuestionUseCase;
import com.example.encuestas_api.questions.application.port.out.LoadQuestionPort;
import com.example.encuestas_api.questions.domain.model.Question;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GetQuestionService implements GetQuestionUseCase {
    private final LoadQuestionPort loadPort;
    public GetQuestionService(LoadQuestionPort loadPort){ this.loadPort = loadPort; }
    @Override public Question handle(Long questionId){ return loadPort.loadById(questionId).orElseThrow(); }
}
