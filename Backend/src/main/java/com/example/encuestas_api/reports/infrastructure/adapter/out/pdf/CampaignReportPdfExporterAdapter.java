package com.example.encuestas_api.reports.infrastructure.adapter.out.pdf;

import com.example.encuestas_api.reports.application.port.out.CampaignReportPdfExporterPort;
import com.example.encuestas_api.reports.domain.model.CampaignReport;
import com.example.encuestas_api.reports.domain.model.FormReport;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;

@Component
public class CampaignReportPdfExporterAdapter implements CampaignReportPdfExporterPort {

    @Override
    public byte[] export(CampaignReport cr) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4.rotate(), 36,36,36,36);
            PdfWriter.getInstance(doc, out);
            doc.open();

            Font title = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Font bold  = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
            Font normal= FontFactory.getFont(FontFactory.HELVETICA, 10);

            doc.add(new Paragraph("Campaign Report", title));
            doc.add(new Paragraph("Campaign ID: " + cr.getCampaignId(), normal));
            doc.add(new Paragraph("Generated at: " + (cr.getGeneratedAt()==null?"":cr.getGeneratedAt()), normal));
            doc.add(Chunk.NEWLINE);

            PdfPTable sum = new PdfPTable(2); sum.setWidthPercentage(60);
            sum.addCell(new Phrase("Forms", bold)); sum.addCell(new Phrase(String.valueOf(cr.getFormsCount()), normal));
            sum.addCell(new Phrase("Total submissions", bold)); sum.addCell(new Phrase(String.valueOf(cr.getTotalSubmissions()), normal));
            sum.addCell(new Phrase("Submitted", bold)); sum.addCell(new Phrase(String.valueOf(cr.getSubmittedCount()), normal));
            sum.addCell(new Phrase("Drafts", bold)); sum.addCell(new Phrase(String.valueOf(cr.getDraftCount()), normal));
            sum.addCell(new Phrase("Completion rate", bold)); sum.addCell(new Phrase(String.format(java.util.Locale.US,"%.4f",cr.getCompletionRate()), normal));
            doc.add(sum);
            doc.add(Chunk.NEWLINE);

            PdfPTable forms = new PdfPTable(5); forms.setWidthPercentage(100);
            forms.setWidths(new float[]{18,18,18,18,28});
            forms.addCell(new Phrase("form_id", bold));
            forms.addCell(new Phrase("total", bold));
            forms.addCell(new Phrase("submitted", bold));
            forms.addCell(new Phrase("draft", bold));
            forms.addCell(new Phrase("completion_rate", bold));
            for (FormReport fr : cr.getForms()) {
                forms.addCell(new Phrase(String.valueOf(fr.getFormId()), normal));
                forms.addCell(new Phrase(String.valueOf(fr.getTotalSubmissions()), normal));
                forms.addCell(new Phrase(String.valueOf(fr.getSubmittedCount()), normal));
                forms.addCell(new Phrase(String.valueOf(fr.getDraftCount()), normal));
                forms.addCell(new Phrase(String.format(java.util.Locale.US,"%.4f", fr.getCompletionRate()), normal));
            }
            doc.add(forms);

            doc.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF CampaignReport", e);
        }
    }
}
