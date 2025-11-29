package com.example.encuestas_api.notifications.domain.event;

import java.time.Instant;

public record CampaignReminderEvent(Long campaignId,
                                    Type type,
                                    Instant occurredAt) {
    public enum Type { STARTS_TOMORROW, ENDS_TOMORROW }
}
