package com.sinapipro.api.aftersales.api;

import com.sinapipro.api.aftersales.domain.*;
import com.sinapipro.api.shared.api.PageResponse;
import com.sinapipro.api.shared.error.DomainNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.UUID;

@Tag(name = "After-Sales", description = "Service tickets and technical assistance")
@RestController
@RequestMapping("/api/v1/after-sales/tickets")
public class AfterSalesController {

    private final ServiceTicketRepository ticketRepository;

    public AfterSalesController(ServiceTicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Operation(summary = "List service tickets")
    @GetMapping
    @PreAuthorize("@perm.check('commercial.read')")
    PageResponse<TicketResponse> list(@RequestParam(required = false) String status,
                                      @PageableDefault(size = 20) Pageable pageable) {
        var page = status != null
                ? ticketRepository.findByStatus(status, pageable)
                : ticketRepository.findAll(pageable);
        return PageResponse.from(page.map(TicketResponse::from));
    }

    @Operation(summary = "Get ticket detail")
    @GetMapping("/{id}")
    @PreAuthorize("@perm.check('commercial.read')")
    TicketResponse get(@PathVariable UUID id) {
        return TicketResponse.from(findOrThrow(id));
    }

    @Operation(summary = "Open a service ticket")
    @PostMapping
    @PreAuthorize("@perm.check('commercial.write')")
    ResponseEntity<TicketResponse> create(@Valid @RequestBody CreateTicketRequest req) {
        var ticket = ticketRepository.save(new ServiceTicket(req.unitId(), req.clientName(),
                req.category(), req.description(), req.priority() != null ? req.priority() : "MEDIUM", req.dueDate()));
        return ResponseEntity.created(URI.create("/api/v1/after-sales/tickets/" + ticket.getId()))
                .body(TicketResponse.from(ticket));
    }

    @Operation(summary = "Assign ticket to a technician")
    @PostMapping("/{id}/assign")
    @PreAuthorize("@perm.check('commercial.write')")
    @Transactional
    TicketResponse assign(@PathVariable UUID id, @Valid @RequestBody AssignRequest req) {
        var ticket = findOrThrow(id);
        ticket.assign(req.assignedTo());
        return TicketResponse.from(ticketRepository.save(ticket));
    }

    @Operation(summary = "Resolve a ticket")
    @PostMapping("/{id}/resolve")
    @PreAuthorize("@perm.check('commercial.write')")
    @Transactional
    TicketResponse resolve(@PathVariable UUID id, @Valid @RequestBody ResolveRequest req) {
        var ticket = findOrThrow(id);
        ticket.resolve(req.resolution());
        return TicketResponse.from(ticketRepository.save(ticket));
    }

    @Operation(summary = "Close a resolved ticket")
    @PostMapping("/{id}/close")
    @PreAuthorize("@perm.check('commercial.write')")
    @Transactional
    TicketResponse close(@PathVariable UUID id) {
        var ticket = findOrThrow(id);
        ticket.close();
        return TicketResponse.from(ticketRepository.save(ticket));
    }

    @Operation(summary = "Reopen a ticket")
    @PostMapping("/{id}/reopen")
    @PreAuthorize("@perm.check('commercial.write')")
    @Transactional
    TicketResponse reopen(@PathVariable UUID id) {
        var ticket = findOrThrow(id);
        ticket.reopen();
        return TicketResponse.from(ticketRepository.save(ticket));
    }

    @Operation(summary = "Ticket summary (counts by status)")
    @GetMapping("/summary")
    @PreAuthorize("@perm.check('commercial.read')")
    TicketSummary summary() {
        return new TicketSummary(
                ticketRepository.countByStatus("OPEN"),
                ticketRepository.countByStatus("IN_PROGRESS"),
                ticketRepository.countByStatus("RESOLVED"),
                ticketRepository.countByStatus("CLOSED"));
    }

    private ServiceTicket findOrThrow(UUID id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new DomainNotFoundException("Ticket not found: " + id));
    }

    // --- DTOs ---
    record CreateTicketRequest(UUID unitId, @NotBlank String clientName, @NotBlank String category,
                               @NotBlank String description, String priority, LocalDate dueDate) {}
    record AssignRequest(@NotBlank String assignedTo) {}
    record ResolveRequest(@NotBlank String resolution) {}

    record TicketResponse(UUID id, UUID unitId, String clientName, String category, String description,
                          String priority, String status, String assignedTo, String resolution,
                          String openedAt, LocalDate dueDate, String resolvedAt) {
        static TicketResponse from(ServiceTicket t) {
            return new TicketResponse(t.getId(), t.getUnitId(), t.getClientName(), t.getCategory(),
                    t.getDescription(), t.getPriority(), t.getStatus(), t.getAssignedTo(), t.getResolution(),
                    t.getOpenedAt() != null ? t.getOpenedAt().toString() : null, t.getDueDate(),
                    t.getResolvedAt() != null ? t.getResolvedAt().toString() : null);
        }
    }

    record TicketSummary(long open, long inProgress, long resolved, long closed) {}
}
