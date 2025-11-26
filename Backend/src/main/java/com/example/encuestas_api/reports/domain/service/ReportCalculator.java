package com.example.encuestas_api.reports.domain.service;

import com.example.encuestas_api.reports.domain.exception.UnsupportedQuestionForReportException;
import com.example.encuestas_api.reports.domain.model.*;
import com.example.encuestas_api.reports.domain.valueobject.PairKey;
import com.example.encuestas_api.reports.domain.valueobject.ReportParams;
import com.example.encuestas_api.responses.domain.model.*;
import com.example.encuestas_api.responses.domain.valueobject.*;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class ReportCalculator {

    public FormReport computeFormReport(Long formId,
                                        Map<Long, QuestionSnapshot> snapshots,
                                        List<Submission> submissions,
                                        ReportParams params) {

        Objects.requireNonNull(formId, "formId");
        Objects.requireNonNull(snapshots, "snapshots");
        Objects.requireNonNull(submissions, "submissions");
        Objects.requireNonNull(params, "params");

        long total = submissions.size();
        long submitted = submissions.stream().filter(s -> s.getStatus() == SubmissionStatus.SUBMITTED).count();
        long drafts = total - submitted;

        List<Submission> base = params.isIncludeDrafts()
                ? submissions
                : submissions.stream().filter(s -> s.getStatus() == SubmissionStatus.SUBMITTED).toList();

        List<QuestionReport> questionReports = new ArrayList<>();
        for (QuestionSnapshot q : snapshots.values()) {
            QuestionReport qr = switch (q.getKind()) {
                case CHOICE -> buildChoice(q, base);
                case TRUE_FALSE -> buildTrueFalse(q, base);
                case TEXT -> buildText(q, base);
                case MATCHING -> buildMatching(q, base);
                default -> throw new UnsupportedQuestionForReportException("Tipo no soportado: " + q.getKind());
            };
            questionReports.add(qr);
        }

        double completionRate = total == 0 ? 0.0 : (double) submitted / (double) total;
        return new FormReport(formId, total, submitted, drafts, completionRate, questionReports, Instant.now());
    }

    public CampaignReport aggregateCampaign(Long campaignId, List<FormReport> forms) {
        long totalSubs = forms.stream().mapToLong(FormReport::getTotalSubmissions).sum();
        long submitted  = forms.stream().mapToLong(FormReport::getSubmittedCount).sum();
        long drafts     = forms.stream().mapToLong(FormReport::getDraftCount).sum();
        double rate = totalSubs == 0 ? 0.0 : (double) submitted / (double) totalSubs;

        return new CampaignReport(
                campaignId,
                forms.size(),
                totalSubs,
                submitted,
                drafts,
                rate,
                List.copyOf(forms),
                Instant.now()
        );
    }


    private ChoiceQuestionReport buildChoice(QuestionSnapshot q, List<Submission> subs) {
        long answered = 0;
        Map<Long, Long> counts = new LinkedHashMap<>();
        q.getOptionIds().forEach(id -> counts.put(id, 0L)); // inicializar en orden del snapshot

        for (Submission s : subs) {
            var ansOpt = s.findAnswer(q.getQuestionId());
            if (ansOpt.isEmpty()) continue;
            var ans = ansOpt.get();
            if (!(ans instanceof ChoiceAnswer ca)) continue;

            answered++;
            for (Long sel : ca.getSelectedOptionIds()) {
                if (counts.containsKey(sel)) {
                    counts.put(sel, counts.get(sel) + 1);
                }
            }
        }

        long omitted = subs.size() - answered;
        List<ChoiceOptionStat> opts = q.getOptionIds().stream()
                .map(id -> new ChoiceOptionStat(id, counts.getOrDefault(id, 0L)))
                .toList();

        return new ChoiceQuestionReport(q.getQuestionId(),
                answered, omitted,
                q.getSelectionMode(),
                q.getMinSelections(), q.getMaxSelections(),
                opts);
    }

    private TrueFalseQuestionReport buildTrueFalse(QuestionSnapshot q, List<Submission> subs) {
        long answered = 0, t = 0, f = 0;
        for (Submission s : subs) {
            var ansOpt = s.findAnswer(q.getQuestionId());
            if (ansOpt.isEmpty()) continue;
            var ans = ansOpt.get();
            if (!(ans instanceof TrueFalseAnswer tf)) continue;
            answered++;
            if (tf.isValue()) t++; else f++;
        }
        long omitted = subs.size() - answered;
        return new TrueFalseQuestionReport(q.getQuestionId(), answered, omitted, t, f);
    }

    private TextQuestionReport buildText(QuestionSnapshot q, List<Submission> subs) {
        long answered = 0;
        for (Submission s : subs) {
            var ansOpt = s.findAnswer(q.getQuestionId());
            if (ansOpt.isEmpty()) continue;
            var ans = ansOpt.get();
            if (!(ans instanceof TextAnswer ta)) continue;
            String text = ta.getText();
            if (text != null && !text.isBlank()) answered++;
        }
        long omitted = subs.size() - answered;
        return new TextQuestionReport(q.getQuestionId(),
                answered, omitted,
                q.getTextMode(), q.getMinLength(), q.getMaxLength());
    }

    private MatchingQuestionReport buildMatching(QuestionSnapshot q, List<Submission> subs) {
        long answered = 0;
        Map<PairKey, Long> pairCounts = new LinkedHashMap<>();

        for (Submission s : subs) {
            var ansOpt = s.findAnswer(q.getQuestionId());
            if (ansOpt.isEmpty()) continue;
            var ans = ansOpt.get();
            if (!(ans instanceof MatchingAnswer ma)) continue;

            if (!ma.getPairs().isEmpty()) answered++;
            ma.getPairs().forEach(p -> {
                var key = new PairKey(p.getLeftId(), p.getRightId());
                pairCounts.put(key, pairCounts.getOrDefault(key, 0L) + 1);
            });
        }

        long omitted = subs.size() - answered;
        List<MatchingPairStat> freq = pairCounts.entrySet().stream()
                .map(e -> new MatchingPairStat(e.getKey(), e.getValue()))
                .collect(Collectors.toList());

        return new MatchingQuestionReport(q.getQuestionId(),
                answered, omitted,
                (List<Long>) q.getLeftIds(), (List<Long>) q.getRightIds(),
                freq);
    }
}
