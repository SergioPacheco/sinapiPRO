package com.sinapipro.api.measurement.api;

import com.sinapipro.api.measurement.application.MeasurementService;
import com.sinapipro.api.measurement.application.MeasurementService.*;
import com.sinapipro.api.measurement.domain.*;
import com.sinapipro.api.shared.api.PageResponse;
import com.sinapipro.api.shared.error.DomainNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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

@Tag(name = "Measurements", description = "Periodic work measurements with approval workflow")
@RestController
@RequestMapping("/api/v1/budgets/{budgetId}/measurements")
public class MeasurementController {

    private final MeasurementRepository measurementRepository;
    private final MeasurementService measurementService;

    public MeasurementController(MeasurementRepository measurementRepository, MeasurementService measurementService) {
        this.measurementRepository = measurementRepository;
        this.measurementService = measurementService;
    }

    @Operation(summary = "List measurements for a budget")
    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    PageResponse<MeasurementResponse> list(@PathVariable UUID budgetId, @PageableDefault(size = 20) Pageable pageable) {
        return PageResponse.from(measurementRepository.findByBudgetId(budgetId, pageable).map(MeasurementResponse::from));
    }

    @Operation(summary = "Get measurement detail")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    MeasurementResponse get(@PathVariable UUID budgetId, @PathVariable UUID id) {
        return MeasurementResponse.from(measurementRepository.findById(id)
                .orElseThrow(() -> new DomainNotFoundException("Measurement not found: " + id)));
    }

    @Operation(summary = "Create a measurement")
    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    ResponseEntity<MeasurementResponse> create(@PathVariable UUID budgetId, @Valid @RequestBody CreateMeasurementRequest req) {
        List<ItemInput> items = req.items().stream()
                .map(i -> new ItemInput(i.costCodeId(), i.description(), i.quantity(), i.unitPrice()))
                .toList();
        Measurement saved = measurementService.create(budgetId, req.number(), req.periodStart(), req.periodEnd(),
                req.retentionPct(), items);
        return ResponseEntity.created(URI.create("/api/v1/budgets/" + budgetId + "/measurements/" + saved.getId()))
                .body(MeasurementResponse.from(saved));
    }

    @Operation(summary = "Submit measurement for approval")
    @PostMapping("/{id}/submit")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    MeasurementResponse submit(@PathVariable UUID budgetId, @PathVariable UUID id) {
        return MeasurementResponse.from(measurementService.submit(id));
    }

    @Operation(summary = "Approve measurement")
    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    MeasurementResponse approve(@PathVariable UUID budgetId, @PathVariable UUID id) {
        return MeasurementResponse.from(measurementService.approve(id));
    }

    @Operation(summary = "Summary of all measurements for a budget (approved totals)")
    @GetMapping("/summary")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    MeasurementSummary summary(@PathVariable UUID budgetId) {
        return measurementService.summary(budgetId);
    }

    @Operation(summary = "Cumulative amounts up to a specific measurement")
    @GetMapping("/{id}/cumulative")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    CumulativeResult cumulative(@PathVariable UUID budgetId, @PathVariable UUID id) {
        return measurementService.cumulative(budgetId, id);
    }

    @Operation(summary = "Balance remaining to measure against contracted total")
    @GetMapping("/balance")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    BalanceResult balance(@PathVariable UUID budgetId, @RequestParam BigDecimal contractedTotal) {
        return measurementService.balance(budgetId, contractedTotal);
    }

    // --- DTOs ---
    record CreateMeasurementRequest(@NotNull Integer number, @NotNull LocalDate periodStart,
                                    @NotNull LocalDate periodEnd, @NotNull BigDecimal retentionPct,
                                    @NotNull List<ItemRequest> items) {}
    record ItemRequest(UUID costCodeId, @NotBlank String description, @NotNull @Positive BigDecimal quantity,
                       @NotNull @Positive BigDecimal unitPrice) {}

    record MeasurementResponse(UUID id, Integer number, LocalDate periodStart, LocalDate periodEnd,
                               MeasurementStatus status, BigDecimal retentionPct,
                               BigDecimal grossAmount, BigDecimal netAmount, List<ItemResponse> items) {
        static MeasurementResponse from(Measurement m) {
            return new MeasurementResponse(m.getId(), m.getNumber(), m.getPeriodStart(), m.getPeriodEnd(),
                    m.getStatus(), m.getRetentionPct(), m.getGrossAmount(), m.getNetAmount(),
                    m.getItems().stream().map(i -> new ItemResponse(i.getId(), i.getDescription(),
                            i.getQuantity(), i.getUnitPrice(), i.getAmount())).toList());
        }
    }

    record ItemResponse(UUID id, String description, BigDecimal quantity, BigDecimal unitPrice, BigDecimal amount) {}
}
