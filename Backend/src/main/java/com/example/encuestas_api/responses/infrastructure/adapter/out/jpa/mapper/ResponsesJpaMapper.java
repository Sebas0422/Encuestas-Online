package com.example.encuestas_api.responses.infrastructure.adapter.out.jpa.mapper;

import com.example.encuestas_api.responses.domain.model.*;
import com.example.encuestas_api.responses.domain.valueobject.Respondent;
import com.example.encuestas_api.responses.infrastructure.adapter.out.jpa.entity.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.stream.Collectors;

public final class ResponsesJpaMapper {

    private ResponsesJpaMapper() {}

    public static SubmissionEntity toEntity(Submission s) {
        SubmissionEntity e = new SubmissionEntity();
        e.setId(s.getId());
        e.setFormId(s.getFormId());
        e.setSourceIp(s.getSourceIp());
        e.setStatus(s.getStatus().name());
        e.setCreatedAt(s.getCreatedAt() == null ? Instant.now() : s.getCreatedAt());
        e.setUpdatedAt(s.getUpdatedAt() == null ? e.getCreatedAt() : s.getUpdatedAt());
        e.setSubmittedAt(s.getSubmittedAt());

        switch (s.getRespondent().getType()) {
            case ANONYMOUS -> e.setRespondentType(SubmissionEntity.RespondentType.ANONYMOUS);
            case USER -> { e.setRespondentType(SubmissionEntity.RespondentType.USER); e.setRespondentUserId(s.getRespondent().getUserId()); }
            case EMAIL -> { e.setRespondentType(SubmissionEntity.RespondentType.EMAIL); e.setRespondentEmail(s.getRespondent().getEmail()); }
            case CODE -> { e.setRespondentType(SubmissionEntity.RespondentType.CODE); e.setRespondentCode(s.getRespondent().getCode()); }
        }

        e.getAnswers().clear();
        for (SubmissionAnswer a : s.getAnswers()) {
            var ae = toAnswerEntity(a);
            ae.setSubmission(e);
            e.getAnswers().add(ae);
        }
        return e;
    }

    private static SubmissionAnswerEntity toAnswerEntity(SubmissionAnswer a) {
        if (a instanceof ChoiceAnswer ca) {
            var e = new ChoiceAnswerEntity();
            e.setQuestionId(ca.getQuestionId());
            e.setQuestionVersion(ca.getQuestionVersion());
            e.setSelectedOptionIds(new java.util.LinkedHashSet<>(ca.getSelectedOptionIds()));
            return e;
        }
        if (a instanceof TrueFalseAnswer tf) {
            var e = new TrueFalseAnswerEntity();
            e.setQuestionId(tf.getQuestionId());
            e.setQuestionVersion(tf.getQuestionVersion());
            e.setValue(tf.isValue());
            return e;
        }
        if (a instanceof TextAnswer ta) {
            var e = new TextAnswerEntity();
            e.setQuestionId(ta.getQuestionId());
            e.setQuestionVersion(ta.getQuestionVersion());
            e.setText(ta.getText());
            return e;
        }
        if (a instanceof MatchingAnswer ma) {
            var e = new MatchingAnswerEntity();
            e.setQuestionId(ma.getQuestionId());
            e.setQuestionVersion(ma.getQuestionVersion());
            var pairs = new ArrayList<MatchingAnswerEntity.PairEmb>();
            ma.getPairs().forEach(p -> pairs.add(new MatchingAnswerEntity.PairEmb(p.getLeftId(), p.getRightId())));
            e.setPairs(pairs);
            return e;
        }
        throw new IllegalArgumentException("Tipo de respuesta no soportado: " + a.getClass());
    }

    public static Submission toDomain(SubmissionEntity e) {
        Respondent r = switch (e.getRespondentType()) {
            case ANONYMOUS -> Respondent.anonymous();
            case USER      -> Respondent.user(e.getRespondentUserId());
            case EMAIL     -> Respondent.email(e.getRespondentEmail());
            case CODE      -> Respondent.code(e.getRespondentCode());
        };

        Submission s = new Submission(e.getFormId(), r);
        s.setId(e.getId());
        s.setSourceIp(e.getSourceIp());

        e.getAnswers().forEach(ae -> s.addOrReplaceAnswer(toDomainAnswer(ae)));

        forceField(s, "status",      SubmissionStatus.valueOf(e.getStatus()));
        forceField(s, "createdAt",   e.getCreatedAt());
        forceField(s, "updatedAt",   e.getUpdatedAt());
        forceField(s, "submittedAt", e.getSubmittedAt());

        return s;
    }

    private static void forceField(Submission s, String field, Object value) {
        try {
            var f = Submission.class.getDeclaredField(field);
            f.setAccessible(true);
            f.set(s, value);
        } catch (Exception ignored) {}
    }


    private static SubmissionAnswer toDomainAnswer(SubmissionAnswerEntity ae) {
        if (ae instanceof ChoiceAnswerEntity ce) {
            return new ChoiceAnswer(ce.getQuestionId(), ce.getQuestionVersion(), ce.getSelectedOptionIds());
        }
        if (ae instanceof TrueFalseAnswerEntity te) {
            return new TrueFalseAnswer(te.getQuestionId(), te.getQuestionVersion(), te.isValue());
        }
        if (ae instanceof TextAnswerEntity tx) {
            return new TextAnswer(tx.getQuestionId(), tx.getQuestionVersion(), tx.getText());
        }
        if (ae instanceof MatchingAnswerEntity me) {
            var pairs = me.getPairs().stream()
                    .map(p -> new com.example.encuestas_api.responses.domain.model.MatchingPair(p.getLeftId(), p.getRightId()))
                    .collect(Collectors.toList());
            return new MatchingAnswer(me.getQuestionId(), me.getQuestionVersion(), pairs);
        }
        throw new IllegalArgumentException("Tipo de respuesta no soportado: " + ae.getClass());
    }
}
