package com.example.encuestas_api.responses.application.usecase;

import com.example.encuestas_api.responses.application.dto.SaveTextAnswerCommand;
import com.example.encuestas_api.responses.application.port.in.SaveTextAnswerUseCase;
import com.example.encuestas_api.responses.application.port.out.FindSubmissionPort;
import com.example.encuestas_api.responses.application.port.out.SaveSubmissionPort;
import com.example.encuestas_api.responses.domain.model.Submission;
import com.example.encuestas_api.responses.domain.model.TextAnswer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SaveTextAnswerService implements SaveTextAnswerUseCase {

    private final FindSubmissionPort findPort;
    private final SaveSubmissionPort savePort;

    public SaveTextAnswerService(FindSubmissionPort findPort, SaveSubmissionPort savePort) {
        this.findPort = findPort;
        this.savePort = savePort;
    }

    @Override
    public Submission handle(SaveTextAnswerCommand cmd) {
        Submission s = findPort.findById(cmd.submissionId())
                .orElseThrow(() -> new IllegalArgumentException("Submission no encontrada"));
        s.addOrReplaceAnswer(new TextAnswer(cmd.questionId(), null, cmd.text()));
        return savePort.save(s);
    }
}
