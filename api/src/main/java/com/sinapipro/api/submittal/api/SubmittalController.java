package com.sinapipro.api.submittal.api;

import com.sinapipro.api.shared.api.PageResponse;
import com.sinapipro.api.shared.error.DomainNotFoundException;
import com.sinapipro.api.submittal.domain.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Tag(name = "Submittals", description = "Technical document approval workflow (shop drawings, samples)")
@RestController
@RequestMapping("/api/v1/projects/{projectId}/submittals")
public class SubmittalController {

    private final SubmittalRepository repository;
    public SubmittalController(SubmittalRepository repository) { this.repository = repository; }

    @Operation(summary = "List submittals") @GetMapping
    @PreAuthorize("@perm.check('budget.read')")
    PageResponse<SubmittalResponse> list(@PathVariable UUID projectId, @PageableDefault(size = 20) Pageable pageable) {
        return PageResponse.from(repository.findByBudgetId(projectId, pageable).map(SubmittalResponse::from));
    }

    @Operation(summary = "Create submittal") @PostMapping
    @PreAuthorize("@perm.check('budget.write')")
    ResponseEntity<SubmittalResponse> create(@PathVariable UUID projectId, @Valid @RequestBody CreateRequest req) {
        int num = repository.countByBudgetId(projectId) + 1;
        Submittal s = repository.save(new Submittal(projectId, num, req.title(), req.specSection(),
                req.type(), req.submittedBy(), req.assignedTo(), req.dueDate()));
        return ResponseEntity.created(URI.create("/api/v1/budgets/" + projectId + "/submittals/" + s.getId()))
                .body(SubmittalResponse.from(s));
    }

    @Operation(summary = "Submit for review") @PostMapping("/{id}/submit")
    @PreAuthorize("@perm.check('budget.write')") @Transactional
    SubmittalResponse submit(@PathVariable UUID projectId, @PathVariable UUID id) {
        Submittal s = findOrThrow(id); s.submit(); return SubmittalResponse.from(repository.save(s));
    }

    @Operation(summary = "Approve") @PostMapping("/{id}/approve")
    @PreAuthorize("@perm.check('budget.write')") @Transactional
    SubmittalResponse approve(@PathVariable UUID projectId, @PathVariable UUID id, @RequestBody(required = false) ReviewRequest req) {
        Submittal s = findOrThrow(id); s.approve(req != null ? req.notes() : null); return SubmittalResponse.from(repository.save(s));
    }

    @Operation(summary = "Approve as noted") @PostMapping("/{id}/approve-as-noted")
    @PreAuthorize("@perm.check('budget.write')") @Transactional
    SubmittalResponse approveAsNoted(@PathVariable UUID projectId, @PathVariable UUID id, @Valid @RequestBody ReviewRequest req) {
        Submittal s = findOrThrow(id); s.approveAsNoted(req.notes()); return SubmittalResponse.from(repository.save(s));
    }

    @Operation(summary = "Reject") @PostMapping("/{id}/reject")
    @PreAuthorize("@perm.check('budget.write')") @Transactional
    SubmittalResponse reject(@PathVariable UUID projectId, @PathVariable UUID id, @Valid @RequestBody ReviewRequest req) {
        Submittal s = findOrThrow(id); s.reject(req.notes()); return SubmittalResponse.from(repository.save(s));
    }

    @Operation(summary = "Revise and resubmit") @PostMapping("/{id}/revise")
    @PreAuthorize("@perm.check('budget.write')") @Transactional
    SubmittalResponse revise(@PathVariable UUID projectId, @PathVariable UUID id, @Valid @RequestBody ReviewRequest req) {
        Submittal s = findOrThrow(id); s.reviseAndResubmit(req.notes()); return SubmittalResponse.from(repository.save(s));
    }

    private Submittal findOrThrow(UUID id) {
        return repository.findById(id).orElseThrow(() -> new DomainNotFoundException("Submittal not found: " + id));
    }

    record CreateRequest(@NotBlank String title, String specSection, @NotBlank String type,
                         String submittedBy, String assignedTo, LocalDate dueDate) {}
    record ReviewRequest(@NotBlank String notes) {}
    record SubmittalResponse(UUID id, Integer number, String title, String specSection, String type,
                             SubmittalStatus status, String assignedTo, LocalDate dueDate,
                             Instant submittedAt, Instant reviewedAt, String reviewerNotes) {
        static SubmittalResponse from(Submittal s) {
            return new SubmittalResponse(s.getId(), s.getNumber(), s.getTitle(), s.getSpecSection(),
                    s.getType(), s.getStatus(), s.getAssignedTo(), s.getDueDate(),
                    s.getSubmittedAt(), s.getReviewedAt(), s.getReviewerNotes());
        }
    }
}
