package com.sinapipro.api.schedule.api;

import com.sinapipro.api.budget.domain.Budget;
import com.sinapipro.api.budget.domain.BudgetRepository;
import com.sinapipro.api.schedule.application.CriticalPathService;
import com.sinapipro.api.schedule.application.SCurveService;
import com.sinapipro.api.schedule.domain.ActivityDependency;
import com.sinapipro.api.schedule.domain.ActivityDependencyRepository;
import com.sinapipro.api.schedule.domain.ScheduleActivity;
import com.sinapipro.api.schedule.domain.ScheduleActivityRepository;
import com.sinapipro.api.shared.error.DomainNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Tag(name = "Schedule", description = "Project schedule, activities and S-Curve")
@RestController
@RequestMapping("/api/v1/budgets/{budgetId}/schedule")
public class ScheduleController {

    private final ScheduleActivityRepository activityRepository;
    private final ActivityDependencyRepository dependencyRepository;
    private final BudgetRepository budgetRepository;
    private final SCurveService sCurveService;
    private final CriticalPathService criticalPathService;

    public ScheduleController(ScheduleActivityRepository activityRepository,
                              ActivityDependencyRepository dependencyRepository,
                              BudgetRepository budgetRepository, SCurveService sCurveService,
                              CriticalPathService criticalPathService) {
        this.activityRepository = activityRepository;
        this.dependencyRepository = dependencyRepository;
        this.budgetRepository = budgetRepository;
        this.sCurveService = sCurveService;
        this.criticalPathService = criticalPathService;
    }

    @Operation(summary = "List schedule activities")
    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    List<ActivityResponse> list(@PathVariable UUID budgetId) {
        return activityRepository.findByBudgetIdOrderBySortOrder(budgetId).stream().map(ActivityResponse::from).toList();
    }

    @Operation(summary = "Create a schedule activity")
    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    ResponseEntity<ActivityResponse> create(@PathVariable UUID budgetId, @Valid @RequestBody CreateActivityRequest req) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new DomainNotFoundException("Budget not found: " + budgetId));
        ScheduleActivity activity = activityRepository.save(
                new ScheduleActivity(budget, req.name(), req.plannedStart(), req.plannedEnd(), req.weight(), req.sortOrder()));
        return ResponseEntity.created(URI.create("/api/v1/budgets/" + budgetId + "/schedule/" + activity.getId()))
                .body(ActivityResponse.from(activity));
    }

    @Operation(summary = "Update activity progress")
    @PatchMapping("/{activityId}/progress")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    ActivityResponse updateProgress(@PathVariable UUID budgetId, @PathVariable UUID activityId,
                                    @Valid @RequestBody UpdateProgressRequest req) {
        ScheduleActivity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new DomainNotFoundException("Activity not found: " + activityId));
        activity.updateProgress(req.progressPct(), req.actualStart(), req.actualEnd());
        return ActivityResponse.from(activityRepository.save(activity));
    }

    @Operation(summary = "Delete an activity")
    @DeleteMapping("/{activityId}")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable UUID budgetId, @PathVariable UUID activityId) {
        activityRepository.deleteById(activityId);
    }

    @Operation(summary = "S-Curve data (planned vs actual cumulative by month)")
    @GetMapping("/s-curve")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    SCurveService.SCurveData sCurve(@PathVariable UUID budgetId) {
        return sCurveService.calculate(budgetId);
    }

    @Operation(summary = "Critical path analysis (CPM)")
    @GetMapping("/critical-path")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    CriticalPathService.CriticalPathResult criticalPath(@PathVariable UUID budgetId) {
        return criticalPathService.calculate(budgetId);
    }

    @Operation(summary = "Add dependency between activities")
    @PostMapping("/dependencies")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @ResponseStatus(HttpStatus.CREATED)
    DependencyResponse addDependency(@PathVariable UUID budgetId, @Valid @RequestBody CreateDependencyRequest req) {
        ScheduleActivity predecessor = activityRepository.findById(req.predecessorId())
                .orElseThrow(() -> new DomainNotFoundException("Activity not found: " + req.predecessorId()));
        ScheduleActivity successor = activityRepository.findById(req.successorId())
                .orElseThrow(() -> new DomainNotFoundException("Activity not found: " + req.successorId()));
        ActivityDependency dep = dependencyRepository.save(
                new ActivityDependency(predecessor, successor, req.type() != null ? req.type() : "FS"));
        return new DependencyResponse(dep.getId(), dep.getPredecessor().getId(), dep.getSuccessor().getId(), dep.getType());
    }

    // --- DTOs ---
    record CreateActivityRequest(@NotBlank String name, @NotNull LocalDate plannedStart, @NotNull LocalDate plannedEnd,
                                 @NotNull BigDecimal weight, @NotNull Integer sortOrder) {}
    record UpdateProgressRequest(@NotNull BigDecimal progressPct, LocalDate actualStart, LocalDate actualEnd) {}
    record CreateDependencyRequest(@NotNull UUID predecessorId, @NotNull UUID successorId, String type) {}

    record ActivityResponse(UUID id, String name, LocalDate plannedStart, LocalDate plannedEnd,
                            LocalDate actualStart, LocalDate actualEnd, BigDecimal weight,
                            BigDecimal progressPct, Integer sortOrder) {
        static ActivityResponse from(ScheduleActivity a) {
            return new ActivityResponse(a.getId(), a.getName(), a.getPlannedStart(), a.getPlannedEnd(),
                    a.getActualStart(), a.getActualEnd(), a.getWeight(), a.getProgressPct(), a.getSortOrder());
        }
    }

    record DependencyResponse(UUID id, UUID predecessorId, UUID successorId, String type) {}
}
