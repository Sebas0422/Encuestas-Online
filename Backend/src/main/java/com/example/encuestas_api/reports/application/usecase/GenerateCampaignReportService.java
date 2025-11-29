package com.example.encuestas_api.reports.application.usecase;

import com.example.encuestas_api.reports.application.dto.CampaignReportQuery;
import com.example.encuestas_api.reports.application.port.in.GenerateCampaignReportUseCase;
import com.example.encuestas_api.reports.application.port.out.ListFormSubmissionsPort;
import com.example.encuestas_api.reports.application.port.out.ListFormsByCampaignPort;
import com.example.encuestas_api.reports.application.port.out.QuestionsSnapshotPort;
import com.example.encuestas_api.reports.domain.model.CampaignReport;
import com.example.encuestas_api.reports.domain.model.FormReport;
import com.example.encuestas_api.reports.domain.service.ReportCalculator;
import com.example.encuestas_api.reports.domain.valueobject.ReportParams;
import com.example.encuestas_api.responses.domain.model.Submission;
import com.example.encuestas_api.responses.domain.model.SubmissionStatus;
import com.example.encuestas_api.responses.domain.valueobject.QuestionSnapshot;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class GenerateCampaignReportService implements GenerateCampaignReportUseCase {

    private final ListFormsByCampaignPort formsPort;
    private final QuestionsSnapshotPort snapshotsPort;
    private final ListFormSubmissionsPort submissionsPort;
    private final ReportCalculator calculator;

    public GenerateCampaignReportService(ListFormsByCampaignPort formsPort,
                                         QuestionsSnapshotPort snapshotsPort,
                                         ListFormSubmissionsPort submissionsPort,
                                         ReportCalculator calculator) {
        this.formsPort = formsPort;
        this.snapshotsPort = snapshotsPort;
        this.submissionsPort = submissionsPort;
        this.calculator = calculator;
    }

    @Override
    public CampaignReport handle(CampaignReportQuery query) {
        var formIds = formsPort.findFormIdsByCampaign(query.campaignId());

        List<FormReport> forms = new ArrayList<>(formIds.size());
        var params = new ReportParams(query.includeDrafts());

        for (Long formId : formIds) {
            Map<Long, QuestionSnapshot> snapshots = snapshotsPort.byFormId(formId);
            List<Submission> all = submissionsPort.findByFormId(formId);
            List<Submission> base = query.includeDrafts()
                    ? all
                    : all.stream().filter(s -> s.getStatus() == SubmissionStatus.SUBMITTED).toList();

            var fr = calculator.computeFormReport(formId, snapshots, base, params);
            forms.add(fr);
        }

        return calculator.aggregateCampaign(query.campaignId(), forms);
    }
}
