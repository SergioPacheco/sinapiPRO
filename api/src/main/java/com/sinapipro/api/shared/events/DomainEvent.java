package com.sinapipro.api.shared.events;

import module java.base;

/**
 * Sealed domain event hierarchy — enables exhaustive pattern matching on event types.
 */
public sealed interface DomainEvent {

    Instant timestamp();
    String domain();
    String entityId();
    String message();

    record Created(Instant timestamp, String domain, String entityId, String message) implements DomainEvent {}
    record Updated(Instant timestamp, String domain, String entityId, String message) implements DomainEvent {}
    record Deleted(Instant timestamp, String domain, String entityId, String message) implements DomainEvent {}
    record Snapshot(Instant timestamp, String domain, String entityId, String message) implements DomainEvent {}
    record Heartbeat(Instant timestamp) implements DomainEvent {
        public String domain() { return "system"; }
        public String entityId() { return ""; }
        public String message() { return "keepalive"; }
    }

    static DomainEvent heartbeat() {
        return new Heartbeat(Instant.now());
    }

    default String eventName() {
        return switch (this) {
            case Created _   -> "created";
            case Updated _   -> "updated";
            case Deleted _   -> "deleted";
            case Snapshot _  -> "snapshot";
            case Heartbeat _ -> "heartbeat";
        };
    }
}
