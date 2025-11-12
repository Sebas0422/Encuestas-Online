package com.example.encuestas_api.forms.application.port.in;

import com.example.encuestas_api.common.dto.PagedResult;
import com.example.encuestas_api.forms.application.dto.SectionListQuery;
import com.example.encuestas_api.forms.domain.model.Section;

public interface ListSectionsUseCase {
    PagedResult<Section> handle(SectionListQuery query);
}
