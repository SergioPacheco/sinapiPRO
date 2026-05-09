package com.sinapipro.api.shared.events;

import module java.base;

import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Component
public class OperationEventPublisher {

    private final Sinks.Many<DomainEvent> sink = Sinks.many().multicast().onBackpressureBuffer();

    public void publish(String domain, OperationEventType type, String entityId, String message) {
        var event = switch (type) {
            case CREATED   -> new DomainEvent.Created(Instant.now(), domain, entityId, message);
            case UPDATED   -> new DomainEvent.Updated(Instant.now(), domain, entityId, message);
            case DELETED   -> new DomainEvent.Deleted(Instant.now(), domain, entityId, message);
            case SNAPSHOT  -> new DomainEvent.Snapshot(Instant.now(), domain, entityId, message);
            case HEARTBEAT -> DomainEvent.heartbeat();
        };
        sink.tryEmitNext(event);
    }

    public Flux<ServerSentEvent<DomainEvent>> stream() {
        Flux<DomainEvent> heartbeat = Flux.interval(Duration.ofSeconds(15))
                .map(_ -> DomainEvent.heartbeat());

        return Flux.merge(sink.asFlux(), heartbeat)
                .map(event -> ServerSentEvent.<DomainEvent>builder()
                        .id(event.entityId() + ":" + event.timestamp().toEpochMilli())
                        .event(event.eventName())
                        .data(event)
                        .build());
    }
}
