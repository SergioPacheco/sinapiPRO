package com.sinapipro.api.dailylog.api;

import com.sinapipro.api.dailylog.application.DailyLogService;
import com.sinapipro.api.dailylog.application.DailyLogService.*;
import com.sinapipro.api.dailylog.domain.*;
import com.sinapipro.api.shared.api.PageResponse;
import com.sinapipro.api.shared.error.DomainNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Tag(name = "Daily Log", description = "Daily construction log (diário de obra)")
@RestController
@RequestMapping("/api/v1/budgets/{budgetId}/daily-logs")
public class DailyLogController {

    private final DailyLogRepository dailyLogRepository;
    private final DailyLogService dailyLogService;

    public DailyLogController(DailyLogRepository dailyLogRepository, DailyLogService dailyLogService) {
        this.dailyLogRepository = dailyLogRepository;
        this.dailyLogService = dailyLogService;
    }

    @Operation(summary = "List daily logs for a budget")
    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    PageResponse<DailyLogResponse> list(@PathVariable UUID budgetId, @PageableDefault(size = 20) Pageable pageable) {
        return PageResponse.from(dailyLogRepository.findByBudgetId(budgetId, pageable).map(DailyLogResponse::from));
    }

    @Operation(summary = "Get daily log detail")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    DailyLogResponse get(@PathVariable UUID budgetId, @PathVariable UUID id) {
        return DailyLogResponse.from(dailyLogRepository.findById(id)
                .orElseThrow(() -> new DomainNotFoundException("Daily log not found: " + id)));
    }

    @Operation(summary = "Create a daily log entry")
    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    ResponseEntity<DailyLogResponse> create(@PathVariable UUID budgetId, @Valid @RequestBody CreateDailyLogRequest req) {
        List<LaborInput> labor = req.labor() != null
                ? req.labor().stream().map(l -> new LaborInput(l.workerName(), l.role(), l.hours())).toList() : null;
        List<EquipmentInput> equipment = req.equipment() != null
                ? req.equipment().stream().map(e -> new EquipmentInput(e.equipmentName(), e.hoursUsed(), e.hoursIdle())).toList() : null;
        List<OccurrenceInput> occurrences = req.occurrences() != null
                ? req.occurrences().stream().map(o -> new OccurrenceInput(o.type(), o.description())).toList() : null;

        DailyLog saved = dailyLogService.create(budgetId, req.logDate(), req.weatherMorning(),
                req.weatherAfternoon(), req.observations(), labor, equipment, occurrences);
        return ResponseEntity.created(java.net.URI.create("/api/v1/budgets/" + budgetId + "/daily-logs/" + saved.getId()))
                .body(DailyLogResponse.from(saved));
    }

    @Operation(summary = "Summary of all daily logs (total hours, occurrences)")
    @GetMapping("/summary")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    DailyLogSummary summary(@PathVariable UUID budgetId) {
        return dailyLogService.summary(budgetId);
    }

    // --- DTOs ---
    record CreateDailyLogRequest(@NotNull LocalDate logDate, String weatherMorning, String weatherAfternoon,
                                 String observations, List<LaborEntry> labor, List<EquipmentEntry> equipment,
                                 List<OccurrenceEntry> occurrences) {}
    record LaborEntry(@NotBlank String workerName, @NotBlank String role, @NotNull BigDecimal hours) {}
    record EquipmentEntry(@NotBlank String equipmentName, @NotNull BigDecimal hoursUsed, BigDecimal hoursIdle) {}
    record OccurrenceEntry(@NotBlank String type, @NotBlank String description) {}

    record DailyLogResponse(UUID id, LocalDate logDate, String weatherMorning, String weatherAfternoon,
                            String observations, int laborCount, int equipmentCount, int occurrenceCount) {
        static DailyLogResponse from(DailyLog d) {
            return new DailyLogResponse(d.getId(), d.getLogDate(), d.getWeatherMorning(), d.getWeatherAfternoon(),
                    d.getObservations(), d.getLaborEntries().size(), d.getEquipmentEntries().size(), d.getOccurrences().size());
        }
    }
}
