package com.sinapipro.api.rfi.api;

import com.sinapipro.api.rfi.domain.*;
import com.sinapipro.api.shared.api.PageResponse;
import com.sinapipro.api.shared.error.DomainNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Tag(name = "RFI", description = "Requests for Information with deadline tracking")
@RestController
@RequestMapping("/api/v1/budgets/{budgetId}/rfis")
public class RfiController {

    private final RfiRepository rfiRepository;

    public RfiController(RfiRepository rfiRepository) {
        this.rfiRepository = rfiRepository;
    }

    @Operation(summary = "List RFIs for a budget")
    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    PageResponse<RfiResponse> list(@PathVariable UUID budgetId, @PageableDefault(size = 20) Pageable pageable) {
        return PageResponse.from(rfiRepository.findByBudgetId(budgetId, pageable).map(RfiResponse::from));
    }

    @Operation(summary = "Create an RFI")
    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    ResponseEntity<RfiResponse> create(@PathVariable UUID budgetId, @Valid @RequestBody CreateRfiRequest req) {
        int nextNumber = rfiRepository.countByBudgetId(budgetId) + 1;
        Rfi rfi = rfiRepository.save(new Rfi(budgetId, nextNumber, req.subject(), req.question(),
                req.priority() != null ? req.priority() : "NORMAL", req.assignedTo(), req.createdBy(), req.dueDate()));
        return ResponseEntity.created(URI.create("/api/v1/budgets/" + budgetId + "/rfis/" + rfi.getId()))
                .body(RfiResponse.from(rfi));
    }

    @Operation(summary = "Get RFI detail")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    RfiResponse get(@PathVariable UUID budgetId, @PathVariable UUID id) {
        return RfiResponse.from(rfiRepository.findById(id)
                .orElseThrow(() -> new DomainNotFoundException("RFI not found: " + id)));
    }

    @Operation(summary = "Answer an RFI")
    @PostMapping("/{id}/answer")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @Transactional
    RfiResponse answer(@PathVariable UUID budgetId, @PathVariable UUID id, @Valid @RequestBody AnswerRfiRequest req) {
        Rfi rfi = rfiRepository.findById(id)
                .orElseThrow(() -> new DomainNotFoundException("RFI not found: " + id));
        rfi.respond(req.answer());
        return RfiResponse.from(rfiRepository.save(rfi));
    }

    @Operation(summary = "Close an RFI")
    @PostMapping("/{id}/close")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @Transactional
    RfiResponse close(@PathVariable UUID budgetId, @PathVariable UUID id) {
        Rfi rfi = rfiRepository.findById(id)
                .orElseThrow(() -> new DomainNotFoundException("RFI not found: " + id));
        rfi.close();
        return RfiResponse.from(rfiRepository.save(rfi));
    }

    @Operation(summary = "List overdue RFIs")
    @GetMapping("/overdue")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    List<RfiResponse> overdue(@PathVariable UUID budgetId) {
        return rfiRepository.findByBudgetIdAndStatus(budgetId, RfiStatus.OPEN).stream()
                .filter(Rfi::isOverdue).map(RfiResponse::from).toList();
    }

    // --- DTOs ---
    record CreateRfiRequest(@NotBlank String subject, @NotBlank String question,
                            String priority, String assignedTo, String createdBy, LocalDate dueDate) {}
    record AnswerRfiRequest(@NotBlank String answer) {}

    record RfiResponse(UUID id, Integer number, String subject, String question, String answer,
                       RfiStatus status, String priority, String assignedTo, String createdBy,
                       LocalDate dueDate, Instant answeredAt, boolean overdue) {
        static RfiResponse from(Rfi r) {
            return new RfiResponse(r.getId(), r.getNumber(), r.getSubject(), r.getQuestion(), r.getAnswer(),
                    r.getStatus(), r.getPriority(), r.getAssignedTo(), r.getCreatedBy(),
                    r.getDueDate(), r.getAnsweredAt(), r.isOverdue());
        }
    }
}
