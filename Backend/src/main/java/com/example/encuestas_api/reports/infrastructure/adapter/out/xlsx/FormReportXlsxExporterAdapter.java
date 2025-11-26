package com.example.encuestas_api.reports.infrastructure.adapter.out.xlsx;

import com.example.encuestas_api.reports.application.port.out.FormReportXlsxExporterPort;
import com.example.encuestas_api.reports.domain.model.*;
import com.example.encuestas_api.responses.domain.valueobject.QuestionKind;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;

@Component
public class FormReportXlsxExporterAdapter implements FormReportXlsxExporterPort {

    @Override
    public byte[] export(FormReport r) {
        try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            CellStyle bold = wb.createCellStyle();
            Font f = wb.createFont(); f.setBold(true); bold.setFont(f);

            Sheet summary = wb.createSheet("Summary");
            int row = 0;
            row = writeRow(summary, row, bold, "Form ID", String.valueOf(r.getFormId()));
            row = writeRow(summary, row, null, "Total submissions", String.valueOf(r.getTotalSubmissions()));
            row = writeRow(summary, row, null, "Submitted", String.valueOf(r.getSubmittedCount()));
            row = writeRow(summary, row, null, "Drafts", String.valueOf(r.getDraftCount()));
            row = writeRow(summary, row, null, "Completion rate", String.format(java.util.Locale.US, "%.4f", r.getCompletionRate()));
            row = writeRow(summary, row, null, "Generated at", r.getGeneratedAt()==null?"":r.getGeneratedAt().toString());
            summary.autoSizeColumn(0); summary.autoSizeColumn(1);

            Sheet qsheet = wb.createSheet("Questions");
            int rix = 0;
            Row header = qsheet.createRow(rix++);
            header.createCell(0).setCellValue("question_id");
            header.createCell(1).setCellValue("kind");
            header.createCell(2).setCellValue("answered");
            header.createCell(3).setCellValue("omitted");
            header.createCell(4).setCellValue("extra");

            for (QuestionReport q : r.getQuestions()) {
                Row rr = qsheet.createRow(rix++);
                rr.createCell(0).setCellValue(q.getQuestionId());
                rr.createCell(1).setCellValue(q.getKind().name());
                rr.createCell(2).setCellValue(q.getAnsweredCount());
                rr.createCell(3).setCellValue(q.getOmittedCount());

                String extra = "";
                if (q.getKind() == QuestionKind.CHOICE && q instanceof ChoiceQuestionReport c) {
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
                rr.createCell(4).setCellValue(extra);
            }
            for (int c=0;c<=4;c++) qsheet.autoSizeColumn(c);

            wb.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating XLSX FormReport", e);
        }
    }

    private int writeRow(Sheet s, int r, CellStyle bold, String k, String v) {
        Row row = s.createRow(r++);
        Cell c0 = row.createCell(0);
        if (bold!=null) c0.setCellStyle(bold);
        c0.setCellValue(k);
        row.createCell(1).setCellValue(v==null?"":v);
        return r;
    }
}
