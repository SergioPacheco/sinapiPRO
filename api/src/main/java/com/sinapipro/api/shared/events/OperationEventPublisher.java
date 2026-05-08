package com.sinapipro.api.shared.events;

import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.time.Instant;

@Component
public class OperationEventPublisher {

    private final Sinks.Many<OperationEvent> sink = Sinks.many().multicast().onBackpressureBuffer();

    public void publish(String domain, OperationEventType type, String entityId, String message) {
        sink.tryEmitNext(new OperationEvent(Instant.now(), type, domain, entityId, message));
    }

    public Flux<ServerSentEvent<OperationEvent>> stream() {
        Flux<OperationEvent> heartbeat = Flux.interval(Duration.ofSeconds(15)).map(ignored -> OperationEvent.heartbeat());
        return Flux.merge(sink.asFlux(), heartbeat)
                .map(event -> ServerSentEvent.<OperationEvent>builder()
                        .id(event.entityId() + ":" + event.timestamp().toEpochMilli())
                        .event(event.type().name().toLowerCase())
                        .data(event)
                        .build());
    }
}
