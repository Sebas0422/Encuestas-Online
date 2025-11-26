package com.example.encuestas_api.reports.infrastructure.adapter.in.rest.mapper;

import com.example.encuestas_api.reports.domain.model.*;
import com.example.encuestas_api.reports.infrastructure.adapter.in.rest.dto.CampaignReportResponse;
import com.example.encuestas_api.reports.infrastructure.adapter.in.rest.dto.FormReportResponse;

import java.util.List;

public final class ReportsRestMapper {
    private ReportsRestMapper(){}

    public static FormReportResponse toResponse(FormReport r) {
        var blocks = r.getQuestions().stream().map(qr -> {
            if (qr instanceof ChoiceQuestionReport c) {
                var opts = c.getOptions().stream()
                        .map(o -> new FormReportResponse.ChoiceBlock.OptionStat(o.getOptionId(), o.getCount()))
                        .toList();
                return (FormReportResponse.QuestionBlock) new FormReportResponse.ChoiceBlock(
                        c.getQuestionId(), c.getAnsweredCount(), c.getOmittedCount(),
                        c.getSelectionMode().name(), c.getMinSelections(), c.getMaxSelections(), opts
                );
            }
            if (qr instanceof TrueFalseQuestionReport tf) {
                return (FormReportResponse.QuestionBlock) new FormReportResponse.TrueFalseBlock(
                        tf.getQuestionId(), tf.getAnsweredCount(), tf.getOmittedCount(),
                        tf.getTrueCount(), tf.getFalseCount()
                );
            }
            if (qr instanceof TextQuestionReport tx) {
                return (FormReportResponse.QuestionBlock) new FormReportResponse.TextBlock(
                        tx.getQuestionId(), tx.getAnsweredCount(), tx.getOmittedCount(),
                        tx.getTextMode().name(), tx.getMinLength(), tx.getMaxLength()
                );
            }
            if (qr instanceof MatchingQuestionReport m) {
                var pairs = m.getPairFrequencies().stream()
                        .map(p -> new FormReportResponse.MatchingBlock.PairStat(
                                p.getPair().getLeftId(), p.getPair().getRightId(), p.getCount()))
                        .toList();
                return (FormReportResponse.QuestionBlock) new FormReportResponse.MatchingBlock(
                        m.getQuestionId(), m.getAnsweredCount(), m.getOmittedCount(),
                        m.getLeftIds(), m.getRightIds(), pairs
                );
            }
            // Fallback (no debería ocurrir)
            return null;
        }).toList();

        return new FormReportResponse(
                r.getFormId(),
                r.getTotalSubmissions(),
                r.getSubmittedCount(),
                r.getDraftCount(),
                r.getCompletionRate(),
                r.getGeneratedAt(),
                blocks
        );
    }

    public static CampaignReportResponse toResponse(CampaignReport cr) {
        List<FormReportResponse> forms = cr.getForms().stream()
                .map(ReportsRestMapper::toResponse)
                .toList();

        return new CampaignReportResponse(
                cr.getCampaignId(),
                cr.getFormsCount(),
                cr.getTotalSubmissions(),
                cr.getSubmittedCount(),
                cr.getDraftCount(),
                cr.getCompletionRate(),
                cr.getGeneratedAt(),
                forms
        );
    }
}
