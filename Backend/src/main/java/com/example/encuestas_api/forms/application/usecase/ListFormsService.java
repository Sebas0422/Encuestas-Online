package com.example.encuestas_api.forms.application.usecase;

import com.example.encuestas_api.common.dto.PagedResult;
import com.example.encuestas_api.forms.application.dto.FormListQuery;
import com.example.encuestas_api.forms.application.port.in.ListFormsUseCase;
import com.example.encuestas_api.forms.application.port.out.SearchFormsPort;
import com.example.encuestas_api.forms.domain.model.Form;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ListFormsService implements ListFormsUseCase {
    private final SearchFormsPort searchPort;
    public ListFormsService(SearchFormsPort searchPort){ this.searchPort = searchPort; }
    @Override public PagedResult<Form> handle(FormListQuery query){ return searchPort.search(query); }
}
