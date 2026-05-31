package com.sinapipro.api.finance.api;

import com.sinapipro.api.finance.application.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Tag(name = "Finance Reports", description = "Sprint 7: DRE, Aging, Cost Apportionment, Cash Flow Projection")
@RestController
@RequestMapping("/api/v1/finance")
public class FinanceReportsController {

    private final DreService dreService;
    private final FinanceAgingService agingReportService;
    private final CostApportionmentService costApportionmentService;
    private final FinanceService financeService;

    public FinanceReportsController(DreService dreService, FinanceAgingService agingReportService,
                                    CostApportionmentService costApportionmentService,
                                    FinanceService financeService) {
        this.dreService = dreService;
        this.agingReportService = agingReportService;
        this.costApportionmentService = costApportionmentService;
        this.financeService = financeService;
    }

    // ═══════════════════════════════════════════════════════════
    // 7.7 — DRE por obra
    // ═══════════════════════════════════════════════════════════

    @Operation(summary = "Generate DRE (Income Statement) for a project")
    @GetMapping("/projects/{projectId}/dre")
    @PreAuthorize("@perm.check('finance.read')")
    DreService.DreReport getDre(@PathVariable UUID projectId,
                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return dreService.generate(projectId, startDate, endDate);
    }

    // ═══════════════════════════════════════════════════════════
    // 7.8 — Aging report
    // ═══════════════════════════════════════════════════════════

    @Operation(summary = "Aging report for payables (overdue by bracket)")
    @GetMapping("/aging/payables")
    @PreAuthorize("@perm.check('finance.read')")
    FinanceAgingService.AgingReport payablesAging(@RequestParam(required = false) UUID projectId) {
        return agingReportService.payablesAging(projectId);
    }

    @Operation(summary = "Aging report for receivables (overdue by bracket)")
    @GetMapping("/aging/receivables")
    @PreAuthorize("@perm.check('finance.read')")
    FinanceAgingService.AgingReport receivablesAging(@RequestParam(required = false) UUID projectId) {
        return agingReportService.receivablesAging(projectId);
    }

    // ═══════════════════════════════════════════════════════════
    // 7.4 — Rateio de custos entre obras
    // ═══════════════════════════════════════════════════════════

    record ApportionmentRequest(@NotBlank String description, @NotNull BigDecimal totalAmount,
                                @NotNull LocalDate dueDate, String category, UUID supplierId,
                                @NotNull Map<UUID, BigDecimal> distribution) {}

    record ApportionByRatesRequest(@NotBlank String description, @NotNull BigDecimal totalAmount,
                                   @NotNull LocalDate dueDate, String category, UUID supplierId,
                                   @NotNull List<UUID> projectIds) {}

    @Operation(summary = "Apportion a cost across projects by custom percentages")
    @PostMapping("/apportionment")
    @PreAuthorize("@perm.check('finance.write')")
    ResponseEntity<List<ApportionmentResult>> apportion(@Valid @RequestBody ApportionmentRequest req) {
        var payables = costApportionmentService.apportion(req.description(), req.totalAmount(),
                req.dueDate(), req.category(), req.supplierId(), req.distribution());
        return ResponseEntity.ok(payables.stream()
                .map(p -> new ApportionmentResult(p.getId(), p.getProjectId(), p.getAmount(), p.getDescription()))
                .toList());
    }

    @Operation(summary = "Apportion a cost using project-configured rates")
    @PostMapping("/apportionment/by-rates")
    @PreAuthorize("@perm.check('finance.write')")
    ResponseEntity<List<ApportionmentResult>> apportionByRates(@Valid @RequestBody ApportionByRatesRequest req) {
        var payables = costApportionmentService.apportionByProjectRates(req.description(), req.totalAmount(),
                req.dueDate(), req.category(), req.supplierId(), req.projectIds());
        return ResponseEntity.ok(payables.stream()
                .map(p -> new ApportionmentResult(p.getId(), p.getProjectId(), p.getAmount(), p.getDescription()))
                .toList());
    }

    record ApportionmentResult(UUID payableId, UUID projectId, BigDecimal amount, String description) {}

    // ═══════════════════════════════════════════════════════════
    // 7.6 — Fluxo de caixa projetado (previsto × realizado × projetado)
    // ═══════════════════════════════════════════════════════════

    @Operation(summary = "Cash flow projection for a project (monthly inflows vs outflows)")
    @GetMapping("/projects/{budgetId}/cash-flow-projection")
    @PreAuthorize("@perm.check('finance.read')")
    FinanceService.CashFlowProjection getCashFlowProjection(
            @PathVariable UUID budgetId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return financeService.cashFlowProjection(budgetId, startDate, endDate);
    }

    @Operation(summary = "Consolidated cash flow summary across multiple projects")
    @PostMapping("/cash-flow/consolidated")
    @PreAuthorize("@perm.check('finance.read')")
    FinanceService.ConsolidatedCashFlow getConsolidatedCashFlow(@RequestBody List<UUID> projectIds) {
        return financeService.consolidatedCashFlow(projectIds);
    }
}
