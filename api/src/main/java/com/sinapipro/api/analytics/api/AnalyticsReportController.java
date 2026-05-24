package com.sinapipro.api.analytics.api;

import com.sinapipro.api.analytics.application.*;
import com.sinapipro.api.analytics.application.AgingReportService.AgingReport;
import com.sinapipro.api.analytics.application.CashFlowProjectionService.MonthlyFlow;
import com.sinapipro.api.analytics.application.CostMapService.CostMap;
import com.sinapipro.api.analytics.application.DREService.DRE;
import com.sinapipro.api.analytics.application.ExecutiveDashboardService.ExecutiveDashboard;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Tag(name = "Analytics Reports", description = "DRE, Mapa de Custos, Fluxo de Caixa, Aging, Dashboard Executivo")
@RestController
@RequestMapping("/api/v1/analytics")
@PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
public class AnalyticsReportController {

    private final DREService dreService;
    private final CostMapService costMapService;
    private final CashFlowProjectionService cashFlowService;
    private final AgingReportService agingService;
    private final ExecutiveDashboardService dashboardService;

    public AnalyticsReportController(DREService dreService, CostMapService costMapService,
                                      CashFlowProjectionService cashFlowService,
                                      AgingReportService agingService,
                                      ExecutiveDashboardService dashboardService) {
        this.dreService = dreService;
        this.costMapService = costMapService;
        this.cashFlowService = cashFlowService;
        this.agingService = agingService;
        this.dashboardService = dashboardService;
    }

    @Operation(summary = "DRE — Demonstrativo de Resultado por obra/orçamento")
    @GetMapping("/budgets/{budgetId}/dre")
    DRE dre(@PathVariable UUID budgetId,
            @RequestParam LocalDate from, @RequestParam LocalDate to) {
        return dreService.generate(budgetId, from, to);
    }

    @Operation(summary = "Mapa de custos: orçado × comprometido × realizado")
    @GetMapping("/budgets/{budgetId}/cost-map")
    CostMap costMap(@PathVariable UUID budgetId) {
        return costMapService.generate(budgetId);
    }

    @Operation(summary = "Fluxo de caixa projetado (N meses à frente)")
    @GetMapping("/cash-flow")
    List<MonthlyFlow> cashFlow(@RequestParam(defaultValue = "12") int months) {
        return cashFlowService.project(months);
    }

    @Operation(summary = "Aging report — contas a receber vencidas por faixa")
    @GetMapping("/aging/receivable")
    AgingReport agingReceivable() {
        return agingService.receivableAging();
    }

    @Operation(summary = "Aging report — contas a pagar vencidas por faixa")
    @GetMapping("/aging/payable")
    AgingReport agingPayable() {
        return agingService.payableAging();
    }

    @Operation(summary = "Dashboard executivo — KPIs consolidados")
    @GetMapping("/dashboard")
    ExecutiveDashboard dashboard() {
        return dashboardService.generate();
    }
}
