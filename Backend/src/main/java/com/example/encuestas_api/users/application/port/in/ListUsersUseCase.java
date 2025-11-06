package com.example.encuestas_api.users.application.port.in;

import com.example.encuestas_api.common.dto.PagedResult;
import com.example.encuestas_api.users.application.dto.ListUsersQuery;
import com.example.encuestas_api.users.domain.model.User;

public interface ListUsersUseCase {
    PagedResult<User> handle(ListUsersQuery query);
}
