package com.example.encuestas_api.forms.application.usecase;

import com.example.encuestas_api.common.dto.PagedResult;
import com.example.encuestas_api.forms.application.dto.SectionListQuery;
import com.example.encuestas_api.forms.application.port.in.ListSectionsUseCase;
import com.example.encuestas_api.forms.application.port.out.SearchSectionsPort;
import com.example.encuestas_api.forms.domain.model.Section;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ListSectionsService implements ListSectionsUseCase {

    private final SearchSectionsPort searchPort;

    public ListSectionsService(SearchSectionsPort searchPort) { this.searchPort = searchPort; }

    @Override
    public PagedResult<Section> handle(SectionListQuery query) {
        return searchPort.search(query);
    }
}
