package com.sinapipro.api.schedule.api;

import com.sinapipro.api.budget.domain.Budget;
import com.sinapipro.api.budget.domain.BudgetRepository;
import com.sinapipro.api.schedule.application.CriticalPathService;
import com.sinapipro.api.schedule.application.SCurveService;
import com.sinapipro.api.schedule.application.ScheduleReportService;
import com.sinapipro.api.schedule.domain.Holiday;
import com.sinapipro.api.schedule.domain.HolidayRepository;
import com.sinapipro.api.schedule.domain.ActivityDependency;
import com.sinapipro.api.schedule.domain.ActivityDependencyRepository;
import com.sinapipro.api.schedule.domain.ScheduleActivity;
import com.sinapipro.api.schedule.domain.ScheduleActivityRepository;
import com.sinapipro.api.schedule.domain.ScheduleBaseline;
import com.sinapipro.api.schedule.domain.ScheduleBaselineRepository;
import com.sinapipro.api.shared.error.DomainNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
@RequestMapping("/api/v1/projects/{projectId}/schedule")
public class ScheduleController {

    private final ScheduleActivityRepository activityRepository;
    private final ActivityDependencyRepository dependencyRepository;
    private final ScheduleBaselineRepository baselineRepository;
    private final BudgetRepository budgetRepository;
    private final SCurveService sCurveService;
    private final CriticalPathService criticalPathService;
    private final ScheduleReportService scheduleReportService;
    private final HolidayRepository holidayRepository;

    public ScheduleController(ScheduleActivityRepository activityRepository,
                              ActivityDependencyRepository dependencyRepository,
                              ScheduleBaselineRepository baselineRepository,
                              BudgetRepository budgetRepository, SCurveService sCurveService,
                              CriticalPathService criticalPathService,
                              ScheduleReportService scheduleReportService,
                              HolidayRepository holidayRepository) {
        this.activityRepository = activityRepository;
        this.dependencyRepository = dependencyRepository;
        this.baselineRepository = baselineRepository;
        this.budgetRepository = budgetRepository;
        this.sCurveService = sCurveService;
        this.criticalPathService = criticalPathService;
        this.scheduleReportService = scheduleReportService;
        this.holidayRepository = holidayRepository;
    }

    @Operation(summary = "List schedule activities")
    @GetMapping
    @PreAuthorize("@perm.check('budget.read')")
    List<ActivityResponse> list(@PathVariable UUID projectId) {
        return activityRepository.findByBudgetIdOrderBySortOrder(projectId).stream().map(ActivityResponse::from).toList();
    }

    @Operation(summary = "Create a schedule activity")
    @PostMapping
    @PreAuthorize("@perm.check('budget.write')")
    ResponseEntity<ActivityResponse> create(@PathVariable UUID projectId, @Valid @RequestBody CreateActivityRequest req) {
        Budget budget = budgetRepository.findById(projectId)
                .orElseThrow(() -> new DomainNotFoundException("Budget not found: " + projectId));
        ScheduleActivity activity = activityRepository.save(
                new ScheduleActivity(budget, req.name(), req.plannedStart(), req.plannedEnd(), req.weight(), req.sortOrder()));
        return ResponseEntity.created(URI.create("/api/v1/budgets/" + projectId + "/schedule/" + activity.getId()))
                .body(ActivityResponse.from(activity));
    }

    @Operation(summary = "Update activity progress")
    @PatchMapping("/{activityId}/progress")
    @PreAuthorize("@perm.check('budget.write')")
    ActivityResponse updateProgress(@PathVariable UUID projectId, @PathVariable UUID activityId,
                                    @Valid @RequestBody UpdateProgressRequest req) {
        ScheduleActivity activity = findActivityInProject(projectId, activityId);
        activity.updateProgress(req.progressPct(), req.actualStart(), req.actualEnd());
        return ActivityResponse.from(activityRepository.save(activity));
    }

    @Operation(summary = "Update activity dates (drag & drop on Gantt chart)")
    @PatchMapping("/{activityId}/dates")
    @PreAuthorize("@perm.check('budget.write')")
    ActivityResponse updateDates(@PathVariable UUID projectId, @PathVariable UUID activityId,
                                 @Valid @RequestBody UpdateDatesRequest req) {
        ScheduleActivity activity = findActivityInProject(projectId, activityId);
        activity.updateDates(req.plannedStart(), req.plannedEnd());
        return ActivityResponse.from(activityRepository.save(activity));
    }

    @Operation(summary = "Batch update activity dates (multi-drag on Gantt)")
    @PatchMapping("/batch-dates")
    @PreAuthorize("@perm.check('budget.write')")
    List<ActivityResponse> batchUpdateDates(@PathVariable UUID projectId,
                                           @Valid @RequestBody List<BatchDateEntry> entries) {
        return entries.stream().map(entry -> {
            ScheduleActivity activity = findActivityInProject(projectId, entry.activityId());
            activity.updateDates(entry.plannedStart(), entry.plannedEnd());
            return ActivityResponse.from(activityRepository.save(activity));
        }).toList();
    }

    @Operation(summary = "Gantt chart data (activities with dependencies for rendering)")
    @GetMapping("/gantt")
    @PreAuthorize("@perm.check('budget.read')")
    GanttData ganttData(@PathVariable UUID projectId) {
        var activities = activityRepository.findByBudgetIdOrderBySortOrder(projectId);
        var dependencies = dependencyRepository.findByBudgetId(projectId);
        var ganttActivities = activities.stream().map(a -> new GanttActivity(
                a.getId(), a.getName(), a.getPlannedStart(), a.getPlannedEnd(),
                a.getActualStart(), a.getActualEnd(), a.getProgressPct(), a.getSortOrder()
        )).toList();
        var ganttDeps = dependencies.stream().map(d -> new GanttDependency(
                d.getPredecessor().getId(), d.getSuccessor().getId(), d.getType()
        )).toList();
        return new GanttData(ganttActivities, ganttDeps);
    }

    @Operation(summary = "Delete an activity")
    @DeleteMapping("/{activityId}")
    @PreAuthorize("@perm.check('budget.write')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable UUID projectId, @PathVariable UUID activityId) {
        findActivityInProject(projectId, activityId);
        activityRepository.deleteById(activityId);
    }

    @Operation(summary = "S-Curve data (planned vs actual cumulative by month)")
    @GetMapping("/s-curve")
    @PreAuthorize("@perm.check('budget.read')")
    SCurveService.SCurveData sCurve(@PathVariable UUID projectId) {
        return sCurveService.calculate(projectId);
    }

    @Operation(summary = "Critical path analysis (CPM)")
    @GetMapping("/critical-path")
    @PreAuthorize("@perm.check('budget.read')")
    CriticalPathService.CriticalPathResult criticalPath(@PathVariable UUID projectId) {
        return criticalPathService.calculate(projectId);
    }

    @Operation(summary = "Physical-financial schedule PDF")
    @GetMapping(value = "/reports/physical-financial.pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @PreAuthorize("@perm.check('budget.read')")
    ResponseEntity<byte[]> physicalFinancialReport(@PathVariable UUID projectId) {
        byte[] pdf = scheduleReportService.generatePhysicalFinancialPdf(projectId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=schedule-physical-financial-" + projectId + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @Operation(summary = "Add dependency between activities")
    @PostMapping("/dependencies")
    @PreAuthorize("@perm.check('budget.write')")
    @ResponseStatus(HttpStatus.CREATED)
    DependencyResponse addDependency(@PathVariable UUID projectId, @Valid @RequestBody CreateDependencyRequest req) {
        ScheduleActivity predecessor = findActivityInProject(projectId, req.predecessorId());
        ScheduleActivity successor = findActivityInProject(projectId, req.successorId());
        ActivityDependency dep = dependencyRepository.save(
                new ActivityDependency(predecessor, successor, req.type() != null ? req.type() : "FS"));
        return new DependencyResponse(dep.getId(), dep.getPredecessor().getId(), dep.getSuccessor().getId(), dep.getType());
    }

    // --- Baselines ---

    @Operation(summary = "Save current schedule as a baseline snapshot")
    @PostMapping("/baselines")
    @PreAuthorize("@perm.check('budget.write')")
    ResponseEntity<BaselineResponse> createBaseline(@PathVariable UUID projectId, @Valid @RequestBody CreateBaselineRequest req) {
        var activities = activityRepository.findByBudgetIdOrderBySortOrder(projectId);
        var snapshot = activities.stream().map(a -> new ScheduleBaseline.ActivitySnapshot(
                a.getId(), a.getName(), a.getPlannedStart().toString(), a.getPlannedEnd().toString(),
                a.getWeight().toPlainString(), a.getProgressPct().toPlainString(), a.getSortOrder()
        )).toList();
        var baseline = baselineRepository.save(new ScheduleBaseline(projectId, req.name(), snapshot));
        return ResponseEntity.status(HttpStatus.CREATED).body(BaselineResponse.from(baseline));
    }

    @Operation(summary = "List baselines for this project")
    @GetMapping("/baselines")
    @PreAuthorize("@perm.check('budget.read')")
    List<BaselineResponse> listBaselines(@PathVariable UUID projectId) {
        return baselineRepository.findByProjectIdOrderByCreatedAtDesc(projectId).stream()
                .map(BaselineResponse::from).toList();
    }

    @Operation(summary = "Get baseline detail with activity snapshots")
    @GetMapping("/baselines/{baselineId}")
    @PreAuthorize("@perm.check('budget.read')")
    ScheduleBaseline getBaseline(@PathVariable UUID projectId, @PathVariable UUID baselineId) {
        return findBaselineInProject(projectId, baselineId);
    }

    // --- Auto-distribute dates ---

    @Operation(summary = "Auto-distribute dates sequentially based on duration and dependencies")
    @PostMapping("/distribute-dates")
    @PreAuthorize("@perm.check('budget.write')")
    List<ActivityResponse> distributeDates(@PathVariable UUID projectId, @Valid @RequestBody DistributeDatesRequest req) {
        var activities = activityRepository.findByBudgetIdOrderBySortOrder(projectId);
        if (activities.isEmpty()) return List.of();

        LocalDate cursor = req.startDate();
        for (var activity : activities) {
            long durationDays = java.time.temporal.ChronoUnit.DAYS.between(activity.getPlannedStart(), activity.getPlannedEnd());
            if (durationDays < 1) durationDays = 1;
            activity.updateDates(cursor, cursor.plusDays(durationDays));
            cursor = cursor.plusDays(durationDays);
        }
        activityRepository.saveAll(activities);
        return activities.stream().map(ActivityResponse::from).toList();
    }

    // --- DTOs ---
    record CreateActivityRequest(@NotBlank String name, @NotNull LocalDate plannedStart, @NotNull LocalDate plannedEnd,
                                 @NotNull BigDecimal weight, @NotNull Integer sortOrder) {}
    record UpdateProgressRequest(@NotNull BigDecimal progressPct, LocalDate actualStart, LocalDate actualEnd) {}
    record UpdateDatesRequest(@NotNull LocalDate plannedStart, @NotNull LocalDate plannedEnd) {}
    record BatchDateEntry(@NotNull UUID activityId, @NotNull LocalDate plannedStart, @NotNull LocalDate plannedEnd) {}
    record CreateDependencyRequest(@NotNull UUID predecessorId, @NotNull UUID successorId, String type) {}
    record CreateBaselineRequest(@NotBlank String name) {}
    record DistributeDatesRequest(@NotNull LocalDate startDate) {}

    record GanttActivity(UUID id, String name, LocalDate plannedStart, LocalDate plannedEnd,
                         LocalDate actualStart, LocalDate actualEnd, BigDecimal progressPct, Integer sortOrder) {}
    record GanttDependency(UUID predecessorId, UUID successorId, String type) {}
    record GanttData(List<GanttActivity> activities, List<GanttDependency> dependencies) {}

    record ActivityResponse(UUID id, String name, LocalDate plannedStart, LocalDate plannedEnd,
                            LocalDate actualStart, LocalDate actualEnd, BigDecimal weight,
                            BigDecimal progressPct, Integer sortOrder) {
        static ActivityResponse from(ScheduleActivity a) {
            return new ActivityResponse(a.getId(), a.getName(), a.getPlannedStart(), a.getPlannedEnd(),
                    a.getActualStart(), a.getActualEnd(), a.getWeight(), a.getProgressPct(), a.getSortOrder());
        }
    }

    record DependencyResponse(UUID id, UUID predecessorId, UUID successorId, String type) {}

    record BaselineResponse(UUID id, String name, int activityCount, String createdAt) {
        static BaselineResponse from(ScheduleBaseline b) {
            return new BaselineResponse(b.getId(), b.getName(), b.getSnapshot().size(), b.getCreatedAt().toString());
        }
    }

    // === Task 4.1: Feriados ===

    @Operation(summary = "List holidays for this project")
    @GetMapping("/holidays")
    List<Holiday> listHolidays(@PathVariable UUID projectId) {
        return holidayRepository.findByProjectIdOrderByHolidayDate(projectId);
    }

    @Operation(summary = "Add holiday")
    @PostMapping("/holidays")
    @ResponseStatus(HttpStatus.CREATED)
    Holiday addHoliday(@PathVariable UUID projectId, @RequestBody HolidayRequest req) {
        return holidayRepository.save(new Holiday(projectId, req.date(), req.description(), req.recurring()));
    }

    record HolidayRequest(java.time.LocalDate date, String description, boolean recurring) {}

    // === Task 4.2: Acompanhamento previsto × realizado ===

    @Operation(summary = "Schedule tracking — planned vs actual progress per activity")
    @GetMapping("/tracking")
    List<TrackingLine> tracking(@PathVariable UUID projectId) {
        return activityRepository.findByBudgetIdOrderBySortOrder(projectId).stream()
                .map(a -> new TrackingLine(
                        a.getId(), a.getName(), a.getWeight(), a.getProgressPct(),
                        a.getPlannedStart(), a.getPlannedEnd(), a.getActualStart(), a.getActualEnd(),
                        a.getProgressPct().compareTo(expectedProgress(a)) >= 0 ? "ON_TRACK" : "DELAYED"))
                .toList();
    }

    private java.math.BigDecimal expectedProgress(ScheduleActivity a) {
        var today = java.time.LocalDate.now();
        if (today.isBefore(a.getPlannedStart())) return java.math.BigDecimal.ZERO;
        if (today.isAfter(a.getPlannedEnd())) return new java.math.BigDecimal("100");
        long totalDays = a.getPlannedStart().until(a.getPlannedEnd()).getDays();
        long elapsed = a.getPlannedStart().until(today).getDays();
        if (totalDays == 0) return new java.math.BigDecimal("100");
        return java.math.BigDecimal.valueOf(elapsed * 100.0 / totalDays).setScale(2, java.math.RoundingMode.HALF_UP);
    }

    private ScheduleActivity findActivityInProject(UUID projectId, UUID activityId) {
        ScheduleActivity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new DomainNotFoundException("Activity not found: " + activityId));
        if (!projectId.equals(activity.getBudget().getId())) {
            throw new DomainNotFoundException("Activity not found in project: " + activityId);
        }
        return activity;
    }

    private ScheduleBaseline findBaselineInProject(UUID projectId, UUID baselineId) {
        ScheduleBaseline baseline = baselineRepository.findById(baselineId)
                .orElseThrow(() -> new DomainNotFoundException("Baseline not found: " + baselineId));
        if (!projectId.equals(baseline.getProjectId())) {
            throw new DomainNotFoundException("Baseline not found in project: " + baselineId);
        }
        return baseline;
    }

    record TrackingLine(UUID activityId, String name, java.math.BigDecimal weight, java.math.BigDecimal progressPct,
                        java.time.LocalDate plannedStart, java.time.LocalDate plannedEnd,
                        java.time.LocalDate actualStart, java.time.LocalDate actualEnd, String status) {}
}
