package com.example.encuestas_api.responses.application.usecase;

import com.example.encuestas_api.responses.application.dto.SaveChoiceAnswerCommand;
import com.example.encuestas_api.responses.application.port.in.SaveChoiceAnswerUseCase;
import com.example.encuestas_api.responses.application.port.out.FindSubmissionPort;
import com.example.encuestas_api.responses.application.port.out.SaveSubmissionPort;
import com.example.encuestas_api.responses.domain.model.ChoiceAnswer;
import com.example.encuestas_api.responses.domain.model.Submission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SaveChoiceAnswerService implements SaveChoiceAnswerUseCase {

    private final FindSubmissionPort findPort;
    private final SaveSubmissionPort savePort;

    public SaveChoiceAnswerService(FindSubmissionPort findPort, SaveSubmissionPort savePort) {
        this.findPort = findPort;
        this.savePort = savePort;
    }

    @Override
    public Submission handle(SaveChoiceAnswerCommand cmd) {
        Submission s = findPort.findById(cmd.submissionId())
                .orElseThrow(() -> new IllegalArgumentException("Submission no encontrada"));
        s.addOrReplaceAnswer(new ChoiceAnswer(cmd.questionId(), null, cmd.selectedOptionIds()));
        return savePort.save(s);
    }
}
