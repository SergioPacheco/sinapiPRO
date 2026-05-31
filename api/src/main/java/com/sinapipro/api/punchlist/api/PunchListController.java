package com.sinapipro.api.punchlist.api;

import com.sinapipro.api.punchlist.domain.*;
import com.sinapipro.api.shared.api.PageResponse;
import com.sinapipro.api.shared.error.DomainNotFoundException;
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

@Tag(name = "Punch List", description = "Punch list items for project closeout")
@RestController
@RequestMapping("/api/v1/projects/{projectId}/punch-list")
public class PunchListController {

    private final PunchListRepository repository;

    public PunchListController(PunchListRepository repository) {
        this.repository = repository;
    }

    @Operation(summary = "List punch list items")
    @GetMapping
    @PreAuthorize("@perm.check('budget.read')")
    PageResponse<PunchListResponse> list(@PathVariable UUID projectId,
                                         @RequestParam(required = false) PunchListStatus status,
                                         @PageableDefault(size = 20) Pageable pageable) {
        if (status != null) {
            return PageResponse.from(repository.findByBudgetIdAndStatus(projectId, status, pageable).map(PunchListResponse::from));
        }
        return PageResponse.from(repository.findByBudgetId(projectId, pageable).map(PunchListResponse::from));
    }

    @Operation(summary = "Create a punch list item")
    @PostMapping
    @PreAuthorize("@perm.check('budget.write')")
    ResponseEntity<PunchListResponse> create(@PathVariable UUID projectId, @Valid @RequestBody CreatePunchListRequest req) {
        PunchListItem item = repository.save(new PunchListItem(projectId, req.location(), req.description(),
                req.category(), req.priority(), req.assignedTo(), req.dueDate(), req.createdBy()));
        return ResponseEntity.created(URI.create("/api/v1/budgets/" + projectId + "/punch-list/" + item.getId()))
                .body(PunchListResponse.from(item));
    }

    @Operation(summary = "Mark item as in progress")
    @PostMapping("/{id}/start")
    @PreAuthorize("@perm.check('budget.write')")
    @Transactional
    PunchListResponse start(@PathVariable UUID projectId, @PathVariable UUID id) {
        PunchListItem item = findOrThrow(id);
        item.markInProgress();
        return PunchListResponse.from(repository.save(item));
    }

    @Operation(summary = "Mark item as completed")
    @PostMapping("/{id}/complete")
    @PreAuthorize("@perm.check('budget.write')")
    @Transactional
    PunchListResponse complete(@PathVariable UUID projectId, @PathVariable UUID id) {
        PunchListItem item = findOrThrow(id);
        item.complete();
        return PunchListResponse.from(repository.save(item));
    }

    @Operation(summary = "Verify completed item")
    @PostMapping("/{id}/verify")
    @PreAuthorize("@perm.check('budget.write')")
    @Transactional
    PunchListResponse verify(@PathVariable UUID projectId, @PathVariable UUID id) {
        PunchListItem item = findOrThrow(id);
        item.verify();
        return PunchListResponse.from(repository.save(item));
    }

    @Operation(summary = "Punch list summary (counts by status)")
    @GetMapping("/summary")
    @PreAuthorize("@perm.check('budget.read')")
    PunchListSummary summary(@PathVariable UUID projectId) {
        return new PunchListSummary(
                repository.countByBudgetIdAndStatus(projectId, PunchListStatus.OPEN),
                repository.countByBudgetIdAndStatus(projectId, PunchListStatus.IN_PROGRESS),
                repository.countByBudgetIdAndStatus(projectId, PunchListStatus.COMPLETED),
                repository.countByBudgetIdAndStatus(projectId, PunchListStatus.VERIFIED));
    }

    private PunchListItem findOrThrow(UUID id) {
        return repository.findById(id).orElseThrow(() -> new DomainNotFoundException("Punch list item not found: " + id));
    }

    // --- DTOs ---
    record CreatePunchListRequest(@NotBlank String location, @NotBlank String description,
                                  String category, String priority, String assignedTo,
                                  LocalDate dueDate, String createdBy) {}

    record PunchListResponse(UUID id, String location, String description, String category,
                             String priority, PunchListStatus status, String assignedTo,
                             LocalDate dueDate, Instant completedAt) {
        static PunchListResponse from(PunchListItem i) {
            return new PunchListResponse(i.getId(), i.getLocation(), i.getDescription(), i.getCategory(),
                    i.getPriority(), i.getStatus(), i.getAssignedTo(), i.getDueDate(), i.getCompletedAt());
        }
    }

    record PunchListSummary(long open, long inProgress, long completed, long verified) {}
}
