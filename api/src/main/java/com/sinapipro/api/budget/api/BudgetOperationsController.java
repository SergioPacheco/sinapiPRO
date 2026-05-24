package com.sinapipro.api.budget.api;

import com.sinapipro.api.budget.application.*;
import com.sinapipro.api.budget.application.BulkEntryService.BulkItemEntry;
import com.sinapipro.api.budget.application.FinancialScheduleService.MonthlyAmount;
import com.sinapipro.api.budget.application.PriceAdjustmentByClassService.AdjustmentResult;
import com.sinapipro.api.budget.application.PurchaseAnalysisService.PurchaseAnalysis;
import com.sinapipro.api.budget.domain.Budget;
import com.sinapipro.api.budget.domain.BudgetStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Tag(name = "Budget Operations", description = "Efetivação, digitação rápida, cronograma financeiro, reajuste, análise")
@RestController
@RequestMapping("/api/v1/budgets/{budgetId}")
@PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
public class BudgetOperationsController {

    private final BudgetEffectivenessService effectivenessService;
    private final BulkEntryService bulkEntryService;
    private final FinancialScheduleService scheduleService;
    private final PriceAdjustmentByClassService adjustmentService;
    private final PurchaseAnalysisService purchaseAnalysisService;

    public BudgetOperationsController(BudgetEffectivenessService effectivenessService,
                                       BulkEntryService bulkEntryService,
                                       FinancialScheduleService scheduleService,
                                       PriceAdjustmentByClassService adjustmentService,
                                       PurchaseAnalysisService purchaseAnalysisService) {
        this.effectivenessService = effectivenessService;
        this.bulkEntryService = bulkEntryService;
        this.scheduleService = scheduleService;
        this.adjustmentService = adjustmentService;
        this.purchaseAnalysisService = purchaseAnalysisService;
    }

    @Operation(summary = "Effectuate budget (lock for execution)")
    @PostMapping("/effectuate")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    BudgetStatusResponse effectuate(@PathVariable UUID budgetId) {
        var budget = effectivenessService.effectuate(budgetId);
        return new BudgetStatusResponse(budget.getId(), budget.getStatus());
    }

    @Operation(summary = "Revert effectuation (back to APPROVED)")
    @PostMapping("/revert")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    BudgetStatusResponse revert(@PathVariable UUID budgetId) {
        var budget = effectivenessService.revert(budgetId);
        return new BudgetStatusResponse(budget.getId(), budget.getStatus());
    }

    @Operation(summary = "Bulk insert items into a stage")
    @PostMapping("/stages/{stageId}/bulk-items")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @ResponseStatus(HttpStatus.CREATED)
    BulkInsertResponse bulkInsert(@PathVariable UUID budgetId, @PathVariable UUID stageId,
                                   @Valid @RequestBody @NotEmpty List<BulkItemEntry> entries) {
        var items = bulkEntryService.bulkInsert(budgetId, stageId, entries);
        return new BulkInsertResponse(items.size());
    }

    @Operation(summary = "Get financial schedule (linear distribution)")
    @GetMapping("/financial-schedule")
    List<MonthlyAmount> financialSchedule(@PathVariable UUID budgetId) {
        return scheduleService.generateSchedule(budgetId);
    }

    @Operation(summary = "Get financial schedule with custom weights")
    @PostMapping("/financial-schedule")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    List<MonthlyAmount> financialScheduleWeighted(@PathVariable UUID budgetId,
                                                   @RequestBody List<BigDecimal> weights) {
        return scheduleService.generateSchedule(budgetId, weights);
    }

    @Operation(summary = "Adjust prices by item class/type")
    @PostMapping("/adjust-by-class")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    AdjustmentResult adjustByClass(@PathVariable UUID budgetId,
                                    @RequestBody @NotNull Map<String, BigDecimal> adjustments) {
        return adjustmentService.adjustByClass(budgetId, adjustments);
    }

    @Operation(summary = "Purchase analysis: budgeted vs committed vs realized")
    @GetMapping("/purchase-analysis")
    PurchaseAnalysis purchaseAnalysis(@PathVariable UUID budgetId) {
        return purchaseAnalysisService.analyze(budgetId);
    }

    // DTOs
    record BudgetStatusResponse(UUID id, BudgetStatus status) {}
    record BulkInsertResponse(int itemsInserted) {}
}
