package com.example.encuestas_api.reports.infrastructure.adapter.out.xlsx;

import com.example.encuestas_api.reports.application.port.out.CampaignReportXlsxExporterPort;
import com.example.encuestas_api.reports.domain.model.CampaignReport;
import com.example.encuestas_api.reports.domain.model.FormReport;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;

@Component
public class CampaignReportXlsxExporterAdapter implements CampaignReportXlsxExporterPort {

    @Override
    public byte[] export(CampaignReport cr) {
        try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            CellStyle bold = wb.createCellStyle();
            Font f = wb.createFont(); f.setBold(true); bold.setFont(f);

            Sheet summary = wb.createSheet("Summary");
            int row=0;
            row = writeRow(summary,row,bold,"Campaign ID", String.valueOf(cr.getCampaignId()));
            row = writeRow(summary,row,null,"Forms", String.valueOf(cr.getFormsCount()));
            row = writeRow(summary,row,null,"Total submissions", String.valueOf(cr.getTotalSubmissions()));
            row = writeRow(summary,row,null,"Submitted", String.valueOf(cr.getSubmittedCount()));
            row = writeRow(summary,row,null,"Drafts", String.valueOf(cr.getDraftCount()));
            row = writeRow(summary,row,null,"Completion rate", String.format(java.util.Locale.US,"%.4f", cr.getCompletionRate()));
            row = writeRow(summary,row,null,"Generated at", cr.getGeneratedAt()==null?"":cr.getGeneratedAt().toString());
            summary.autoSizeColumn(0); summary.autoSizeColumn(1);

            Sheet forms = wb.createSheet("Forms");
            Row h = forms.createRow(0);
            h.createCell(0).setCellValue("form_id");
            h.createCell(1).setCellValue("total");
            h.createCell(2).setCellValue("submitted");
            h.createCell(3).setCellValue("draft");
            h.createCell(4).setCellValue("completion_rate");
            int i=1;
            for (FormReport fr : cr.getForms()) {
                Row r = forms.createRow(i++);
                r.createCell(0).setCellValue(fr.getFormId());
                r.createCell(1).setCellValue(fr.getTotalSubmissions());
                r.createCell(2).setCellValue(fr.getSubmittedCount());
                r.createCell(3).setCellValue(fr.getDraftCount());
                r.createCell(4).setCellValue(String.format(java.util.Locale.US,"%.4f", fr.getCompletionRate()));
            }
            for (int c=0;c<=4;c++) forms.autoSizeColumn(c);

            wb.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating XLSX CampaignReport", e);
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
