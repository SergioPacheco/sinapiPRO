package com.sinapipro.api.shared.events;

import java.time.Instant;

public record OperationEvent(Instant timestamp, OperationEventType type, String domain, String entityId, String message) {
    public static OperationEvent heartbeat() {
        return new OperationEvent(Instant.now(), OperationEventType.HEARTBEAT, "system", "", "keepalive");
    }
}
