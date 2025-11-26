package com.example.encuestas_api.reports.infrastructure.adapter.out.pdf;

import com.example.encuestas_api.reports.application.port.out.FormReportPdfExporterPort;
import com.example.encuestas_api.reports.domain.model.*;
import com.example.encuestas_api.responses.domain.valueobject.QuestionKind;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;

@Component
public class FormReportPdfExporterAdapter implements FormReportPdfExporterPort {

    @Override
    public byte[] export(FormReport r) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4, 36, 36, 36, 36);
            PdfWriter.getInstance(doc, out);
            doc.open();

            Font title = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Font bold  = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
            Font normal= FontFactory.getFont(FontFactory.HELVETICA, 10);

            doc.add(new Paragraph("Form Report", title));
            doc.add(new Paragraph("Form ID: " + r.getFormId(), normal));
            doc.add(new Paragraph("Generated at: " + (r.getGeneratedAt()==null?"":r.getGeneratedAt()), normal));
            doc.add(Chunk.NEWLINE);

            PdfPTable sum = new PdfPTable(2); sum.setWidthPercentage(100);
            addRow(sum, bold, "Total submissions", String.valueOf(r.getTotalSubmissions()));
            addRow(sum, bold, "Submitted", String.valueOf(r.getSubmittedCount()));
            addRow(sum, bold, "Drafts", String.valueOf(r.getDraftCount()));
            addRow(sum, bold, "Completion rate", String.format(java.util.Locale.US,"%.4f", r.getCompletionRate()));
            doc.add(sum);
            doc.add(Chunk.NEWLINE);

            doc.add(new Paragraph("Questions", bold));
            PdfPTable qt = new PdfPTable(5); qt.setWidthPercentage(100);
            qt.setWidths(new float[]{18,18,18,18,28});
            header(qt, "question_id","kind","answered","omitted","extra");

            for (QuestionReport q : r.getQuestions()) {
                String extra = "";
                if (q.getKind()== QuestionKind.CHOICE && q instanceof ChoiceQuestionReport c) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("mode=").append(c.getSelectionMode());
                    if (c.getMinSelections()!=null) sb.append(";min=").append(c.getMinSelections());
                    if (c.getMaxSelections()!=null) sb.append(";max=").append(c.getMaxSelections());
                    sb.append(";counts=");
                    for (var opt : c.getOptions()) {
                        sb.append(opt.getOptionId()).append(":").append(opt.getCount()).append("|");
                    }
                    extra = sb.toString();
                } else if (q.getKind()==QuestionKind.TRUE_FALSE && q instanceof TrueFalseQuestionReport t) {
                    extra = "true="+t.getTrueCount()+";false="+t.getFalseCount();
                } else if (q.getKind()==QuestionKind.TEXT && q instanceof TextQuestionReport tx) {
                    extra = "mode="+tx.getTextMode()+";minLen="+tx.getMinLength()+";maxLen="+tx.getMaxLength();
                } else if (q.getKind()==QuestionKind.MATCHING && q instanceof MatchingQuestionReport m) {
                    StringBuilder sb = new StringBuilder("pairs=");
                    for (var p : m.getPairFrequencies()) {
                        sb.append(p.getPair().getLeftId()).append("->").append(p.getPair().getRightId())
                                .append(":").append(p.getCount()).append("|");
                    }
                    extra = sb.toString();
                }
                row(qt, String.valueOf(q.getQuestionId()), q.getKind().name(),
                        String.valueOf(q.getAnsweredCount()), String.valueOf(q.getOmittedCount()), extra);
            }
            doc.add(qt);

            doc.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF FormReport", e);
        }
    }

    private void header(PdfPTable t, String... cols) {
        Font f = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
        for (String c : cols) {
            PdfPCell cell = new PdfPCell(new Phrase(c, f));
            t.addCell(cell);
        }
    }
    private void row(PdfPTable t, String... cols) {
        Font f = FontFactory.getFont(FontFactory.HELVETICA, 9);
        for (String c : cols) t.addCell(new Phrase(c==null?"":c, f));
    }
    private void addRow(PdfPTable t, Font bold, String k, String v) {
        t.addCell(new Phrase(k, bold));
        t.addCell(new Phrase(v==null?"":v, FontFactory.getFont(FontFactory.HELVETICA, 10)));
    }
}
