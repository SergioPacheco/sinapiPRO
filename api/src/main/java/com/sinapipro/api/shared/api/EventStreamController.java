package com.sinapipro.api.shared.api;

import com.sinapipro.api.shared.events.DomainEvent;
import com.sinapipro.api.shared.events.OperationEventPublisher;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@Tag(name = "Events", description = "Real-time Server-Sent Events stream")
@RestController
@RequestMapping("/api/v1/events")
public class EventStreamController {

    private final OperationEventPublisher eventPublisher;

    public EventStreamController(OperationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Operation(summary = "Subscribe to real-time domain events via SSE")
    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    Flux<ServerSentEvent<DomainEvent>> stream() {
        return eventPublisher.stream();
    }
}
