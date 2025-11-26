package com.example.encuestas_api.notifications.application.port.in;

import com.example.encuestas_api.notifications.application.dto.DispatchPendingCommand;

public interface DispatchPendingNotificationsUseCase {
    int handle(DispatchPendingCommand cmd);
}
