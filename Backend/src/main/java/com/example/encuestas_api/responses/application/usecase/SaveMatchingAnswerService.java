package com.example.encuestas_api.responses.application.usecase;

import com.example.encuestas_api.responses.application.dto.SaveMatchingAnswerCommand;
import com.example.encuestas_api.responses.application.port.in.SaveMatchingAnswerUseCase;
import com.example.encuestas_api.responses.application.port.out.FindSubmissionPort;
import com.example.encuestas_api.responses.application.port.out.SaveSubmissionPort;
import com.example.encuestas_api.responses.domain.model.MatchingAnswer;
import com.example.encuestas_api.responses.domain.model.MatchingPair;
import com.example.encuestas_api.responses.domain.model.Submission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class SaveMatchingAnswerService implements SaveMatchingAnswerUseCase {

    private final FindSubmissionPort findPort;
    private final SaveSubmissionPort savePort;

    public SaveMatchingAnswerService(FindSubmissionPort findPort, SaveSubmissionPort savePort) {
        this.findPort = findPort;
        this.savePort = savePort;
    }

    @Override
    public Submission handle(SaveMatchingAnswerCommand cmd) {
        Submission s = findPort.findById(cmd.submissionId())
                .orElseThrow(() -> new IllegalArgumentException("Submission no encontrada"));

        List<MatchingPair> pairs = new ArrayList<>();
        if (cmd.pairs() != null) {
            for (SaveMatchingAnswerCommand.Pair p : cmd.pairs()) {
                pairs.add(new MatchingPair(p.leftId(), p.rightId()));
            }
        }

        s.addOrReplaceAnswer(new MatchingAnswer(cmd.questionId(), null, pairs));
        return savePort.save(s);
    }
}
