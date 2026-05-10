package com.sinapipro.api.timetracking.api;

import com.sinapipro.api.shared.api.PageResponse;
import com.sinapipro.api.timetracking.application.LaborProductivityService;
import com.sinapipro.api.timetracking.domain.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Tag(name = "Time Tracking", description = "Timesheets and labor productivity metrics")
@RestController
@RequestMapping("/api/v1/projects/{projectId}/timesheets")
public class TimeTrackingController {

    private final TimesheetRepository repository;
    private final LaborProductivityService productivityService;

    public TimeTrackingController(TimesheetRepository repository, LaborProductivityService productivityService) {
        this.repository = repository;
        this.productivityService = productivityService;
    }

    @Operation(summary = "List timesheet entries") @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    PageResponse<TimesheetResponse> list(@PathVariable UUID projectId, @PageableDefault(size = 50) Pageable pageable) {
        return PageResponse.from(repository.findByBudgetId(projectId, pageable).map(TimesheetResponse::from));
    }

    @Operation(summary = "Record timesheet entry") @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')") @ResponseStatus(HttpStatus.CREATED)
    TimesheetResponse record(@PathVariable UUID projectId, @Valid @RequestBody CreateTimesheetRequest req) {
        TimesheetEntry entry = repository.save(new TimesheetEntry(projectId, req.costCodeId(), req.workerName(),
                req.role(), req.workDate(), req.regularHours(), req.overtimeHours() != null ? req.overtimeHours() : BigDecimal.ZERO,
                req.hourlyRate(), req.unitsProduced(), req.unitType(), req.notes()));
        return TimesheetResponse.from(entry);
    }

    @Operation(summary = "Labor productivity report (hours/unit, cost by role)") @GetMapping("/productivity")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    LaborProductivityService.ProductivityReport productivity(@PathVariable UUID projectId) {
        return productivityService.calculate(projectId);
    }

    record CreateTimesheetRequest(@NotBlank String workerName, @NotBlank String role, @NotNull LocalDate workDate,
                                  @NotNull BigDecimal regularHours, BigDecimal overtimeHours,
                                  @NotNull BigDecimal hourlyRate, BigDecimal unitsProduced,
                                  String unitType, UUID costCodeId, String notes) {}

    record TimesheetResponse(UUID id, String workerName, String role, LocalDate workDate,
                             BigDecimal regularHours, BigDecimal overtimeHours, BigDecimal totalHours,
                             BigDecimal laborCost, BigDecimal unitsProduced, String unitType) {
        static TimesheetResponse from(TimesheetEntry e) {
            return new TimesheetResponse(e.getId(), e.getWorkerName(), e.getRole(), e.getWorkDate(),
                    e.getRegularHours(), e.getOvertimeHours(), e.getTotalHours(),
                    e.getLaborCost(), e.getUnitsProduced(), e.getUnitType());
        }
    }
}
