package com.example.encuestas_api.forms.application.port.in;

import com.example.encuestas_api.forms.domain.model.AccessMode;
import com.example.encuestas_api.forms.domain.model.Form;

import java.time.Instant;

public interface CreateFormUseCase {
    Form handle(Long campaignId,
                String title,
                String description,
                String coverUrl,
                String themeMode, String themePrimaryColor,
                AccessMode accessMode,
                Instant openAt, Instant closeAt,
                String responseLimitMode, Integer limitedN,
                boolean anonymousMode, boolean allowEditBeforeSubmit, boolean autoSave,
                boolean shuffleQuestions, boolean shuffleOptions, boolean progressBar, boolean paginated);
}
