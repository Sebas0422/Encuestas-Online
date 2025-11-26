package com.example.encuestas_api.responses.infrastructure.adapter.in.rest.mapper;

import com.example.encuestas_api.responses.application.dto.*;
import com.example.encuestas_api.responses.domain.model.Submission;
import com.example.encuestas_api.responses.infrastructure.adapter.in.rest.dto.StartSubmissionRequest;
import com.example.encuestas_api.responses.infrastructure.adapter.in.rest.dto.*;

public final class ResponsesRestMapper {
    private ResponsesRestMapper() {}

    public static StartSubmissionCommand toCommand(Long formId, StartSubmissionRequest r) {
        var type = switch (r.respondentType()) {
            case ANONYMOUS -> StartSubmissionCommand.RespondentType.ANONYMOUS;
            case USER      -> StartSubmissionCommand.RespondentType.USER;
            case EMAIL     -> StartSubmissionCommand.RespondentType.EMAIL;
            case CODE      -> StartSubmissionCommand.RespondentType.CODE;
        };
        return new StartSubmissionCommand(formId, type, r.userId(), r.email(), r.code(), r.sourceIp());
    }

    public static SaveChoiceAnswerCommand toCommand(Long submissionId, SaveChoiceAnswerRequest r) {
        return new SaveChoiceAnswerCommand(submissionId, r.questionId(), r.selectedOptionIds());
    }

    public static SaveTrueFalseAnswerCommand toCommand(Long submissionId, SaveTrueFalseAnswerRequest r) {
        return new SaveTrueFalseAnswerCommand(submissionId, r.questionId(), r.value());
    }

    public static SaveTextAnswerCommand toCommand(Long submissionId, SaveTextAnswerRequest r) {
        return new SaveTextAnswerCommand(submissionId, r.questionId(), r.text());
    }

    public static SaveMatchingAnswerCommand toCommand(Long submissionId, SaveMatchingAnswerRequest r) {
        var pairs = r.pairs() == null ? java.util.List.<SaveMatchingAnswerCommand.Pair>of()
                : r.pairs().stream().map(p -> new SaveMatchingAnswerCommand.Pair(p.leftId(), p.rightId())).toList();
        return new SaveMatchingAnswerCommand(submissionId, r.questionId(), pairs);
    }

    public static SubmissionResponse toResponse(Submission s) {
        String repr = switch (s.getRespondent().getType()) {
            case ANONYMOUS -> "ANONYMOUS";
            case USER      -> "USER:" + s.getRespondent().getUserId();
            case EMAIL     -> "EMAIL:" + s.getRespondent().getEmail();
            case CODE      -> "CODE:" + s.getRespondent().getCode();
        };
        return new SubmissionResponse(
                s.getId(), s.getFormId(), s.getStatus().name(), repr, s.getSourceIp(),
                s.getCreatedAt(), s.getUpdatedAt(), s.getSubmittedAt(),
                s.getAnswers().size()
        );
    }
}
