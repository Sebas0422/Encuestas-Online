package com.example.encuestas_api.forms.application.port.out;

import com.example.encuestas_api.common.dto.PagedResult;
import com.example.encuestas_api.forms.application.dto.SectionListQuery;
import com.example.encuestas_api.forms.domain.model.Section;

public interface SearchSectionsPort {
    PagedResult<Section> search(SectionListQuery query);
}
