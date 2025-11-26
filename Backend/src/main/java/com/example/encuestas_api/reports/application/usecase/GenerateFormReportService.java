package com.example.encuestas_api.reports.application.usecase;

import com.example.encuestas_api.reports.application.dto.FormReportQuery;
import com.example.encuestas_api.reports.application.port.in.GenerateFormReportUseCase;
import com.example.encuestas_api.reports.application.port.out.ListFormSubmissionsPort;
import com.example.encuestas_api.reports.application.port.out.QuestionsSnapshotPort;
import com.example.encuestas_api.reports.domain.model.FormReport;
import com.example.encuestas_api.reports.domain.service.ReportCalculator;
import com.example.encuestas_api.reports.domain.valueobject.ReportParams;
import com.example.encuestas_api.responses.domain.model.Submission;
import com.example.encuestas_api.responses.domain.model.SubmissionStatus;
import com.example.encuestas_api.responses.domain.valueobject.QuestionSnapshot;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class GenerateFormReportService implements GenerateFormReportUseCase {

    private final QuestionsSnapshotPort snapshotsPort;
    private final ListFormSubmissionsPort submissionsPort;
    private final ReportCalculator calculator;

    public GenerateFormReportService(QuestionsSnapshotPort snapshotsPort,
                                     ListFormSubmissionsPort submissionsPort,
                                     ReportCalculator calculator) {
        this.snapshotsPort = snapshotsPort;
        this.submissionsPort = submissionsPort;
        this.calculator = calculator;
    }

    @Override
    public FormReport handle(FormReportQuery query) {
        Map<Long, QuestionSnapshot> snapshots = snapshotsPort.byFormId(query.formId());
        List<Submission> all = submissionsPort.findByFormId(query.formId());

        List<Submission> base = query.includeDrafts()
                ? all
                : all.stream().filter(s -> s.getStatus() == SubmissionStatus.SUBMITTED).toList();

        return calculator.computeFormReport(
                query.formId(),
                snapshots,
                base,
                new ReportParams(query.includeDrafts())
        );
    }
}
