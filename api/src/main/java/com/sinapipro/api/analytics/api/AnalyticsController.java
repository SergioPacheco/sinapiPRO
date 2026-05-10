package com.sinapipro.api.analytics.api;

import com.sinapipro.api.analytics.application.CashFlowService;
import com.sinapipro.api.analytics.application.EarnedValueService;
import com.sinapipro.api.analytics.application.PortfolioAnalyticsService;
import com.sinapipro.api.forecast.application.DelayForecastService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Analytics", description = "Dashboard, EVM, cash flow and portfolio analytics")
@RestController
@RequestMapping("/api/v1/analytics")
@PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
public class AnalyticsController {

    private final PortfolioAnalyticsService portfolioService;
    private final EarnedValueService evmService;
    private final CashFlowService cashFlowService;
    private final DelayForecastService forecastService;

    public AnalyticsController(PortfolioAnalyticsService portfolioService, EarnedValueService evmService,
                               CashFlowService cashFlowService, DelayForecastService forecastService) {
        this.portfolioService = portfolioService;
        this.evmService = evmService;
        this.cashFlowService = cashFlowService;
        this.forecastService = forecastService;
    }

    @Operation(summary = "Portfolio summary (all budgets)")
    @GetMapping("/portfolio")
    PortfolioAnalyticsService.PortfolioSummary portfolio() {
        return portfolioService.summary();
    }

    @Operation(summary = "Earned Value Management for a budget (PV, EV, AC, CPI, SPI, EAC, VAC)")
    @GetMapping("/projects/{projectId}/earned-value")
    EarnedValueService.EvmResult earnedValue(@PathVariable UUID projectId) {
        return evmService.calculate(projectId);
    }

    @Operation(summary = "Cash flow projection (income vs expenses by month)")
    @GetMapping("/projects/{projectId}/cash-flow")
    CashFlowService.CashFlowProjection cashFlow(@PathVariable UUID projectId) {
        return cashFlowService.project(projectId);
    }

    @Operation(summary = "AI Delay Forecast — predicts delays based on schedule performance and weather patterns")
    @GetMapping("/projects/{projectId}/delay-forecast")
    DelayForecastService.DelayForecast delayForecast(@PathVariable UUID projectId) {
        return forecastService.predict(projectId);
    }
}
