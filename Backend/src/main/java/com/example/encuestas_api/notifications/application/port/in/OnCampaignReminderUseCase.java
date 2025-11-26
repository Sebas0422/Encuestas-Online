package com.example.encuestas_api.notifications.application.port.in;

import com.example.encuestas_api.notifications.domain.event.CampaignReminderEvent;

public interface OnCampaignReminderUseCase {
    void handle(CampaignReminderEvent event);
}
