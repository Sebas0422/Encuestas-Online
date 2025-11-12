package com.example.encuestas_api.forms.application.port.in;

import com.example.encuestas_api.common.dto.PagedResult;
import com.example.encuestas_api.forms.application.dto.FormListQuery;
import com.example.encuestas_api.forms.domain.model.Form;

public interface ListFormsUseCase {
    PagedResult<Form> handle(FormListQuery query);
}
