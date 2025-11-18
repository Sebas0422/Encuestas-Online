package com.example.encuestas_api.forms.application.usecase;

import com.example.encuestas_api.forms.application.port.in.CreateFormUseCase;
import com.example.encuestas_api.forms.application.port.out.CheckCampaignExistsPort;
import com.example.encuestas_api.forms.application.port.out.ExistsFormByTitleInCampaignPort;
import com.example.encuestas_api.forms.application.port.out.SaveFormPort;
import com.example.encuestas_api.forms.domain.exception.FormAlreadyExistsException;
import com.example.encuestas_api.forms.domain.model.AccessMode;
import com.example.encuestas_api.forms.domain.model.Form;
import com.example.encuestas_api.forms.domain.model.Theme;
import com.example.encuestas_api.forms.domain.valueobject.AvailabilityWindow;
import com.example.encuestas_api.forms.domain.valueobject.FormTitle;
import com.example.encuestas_api.forms.domain.valueobject.PresentationOptions;
import com.example.encuestas_api.forms.domain.valueobject.ResponseLimitPolicy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

@Service
@Transactional
public class CreateFormService implements CreateFormUseCase {

    private final CheckCampaignExistsPort campaignExists;
    private final ExistsFormByTitleInCampaignPort existsTitle;
    private final SaveFormPort savePort;
    private final Clock clock;

    public CreateFormService(CheckCampaignExistsPort campaignExists,
                             ExistsFormByTitleInCampaignPort existsTitle,
                             SaveFormPort savePort,
                             Clock clock) {
        this.campaignExists = campaignExists;
        this.existsTitle = existsTitle;
        this.savePort = savePort;
        this.clock = clock;
    }

    @Override
    public Form handle(Long campaignId, String title, String description, String coverUrl,
                       String themeMode, String themePrimaryColor,
                       AccessMode accessMode, Instant openAt, Instant closeAt,
                       String responseLimitMode, Integer limitedN,
                       boolean anonymousMode, boolean allowEditBeforeSubmit, boolean autoSave,
                       boolean shuffleQuestions, boolean shuffleOptions, boolean progressBar, boolean paginated) {

        if (campaignId == null || !campaignExists.existsById(campaignId)) {
            throw new IllegalArgumentException("campaignId inválido");
        }
        String t = title == null ? "" : title.trim();
        if (existsTitle.exists(campaignId, t)) {
            throw new FormAlreadyExistsException(t);
        }
        var window = AvailabilityWindow.of(openAt, closeAt);
        var limit = switch (responseLimitMode == null ? "" : responseLimitMode) {
            case "ONE_PER_USER" -> ResponseLimitPolicy.onePerUser();
            case "LIMITED_N"    -> ResponseLimitPolicy.limitedN(limitedN == null ? 1 : limitedN);
            default             -> ResponseLimitPolicy.unlimited();
        };
        var presentation = PresentationOptions.of(shuffleQuestions, shuffleOptions, progressBar, paginated);
        var theme = themeMode == null ? Theme.defaultLight() : new Theme(themeMode, themePrimaryColor);

        var now = Instant.now(clock);
        var form = Form.createNew(
                campaignId,
                FormTitle.of(t),
                description,
                coverUrl,
                theme,
                accessMode == null ? AccessMode.PUBLIC : accessMode,
                window,
                limit,
                anonymousMode,
                allowEditBeforeSubmit,
                autoSave,
                presentation,
                now
        );
        return savePort.save(form);
    }
}
