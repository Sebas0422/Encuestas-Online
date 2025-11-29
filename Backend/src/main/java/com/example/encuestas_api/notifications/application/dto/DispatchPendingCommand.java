package com.example.encuestas_api.notifications.application.dto;

import java.time.Instant;

public record DispatchPendingCommand(
        int maxBatch,
        Instant now
) { }
