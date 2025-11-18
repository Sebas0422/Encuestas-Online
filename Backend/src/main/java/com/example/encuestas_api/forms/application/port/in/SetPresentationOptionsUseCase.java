package com.example.encuestas_api.forms.application.port.in;

import com.example.encuestas_api.forms.domain.model.Form;

public interface SetPresentationOptionsUseCase {
    Form handle(Long formId, boolean shuffleQuestions, boolean shuffleOptions, boolean progressBar, boolean paginated);
}
