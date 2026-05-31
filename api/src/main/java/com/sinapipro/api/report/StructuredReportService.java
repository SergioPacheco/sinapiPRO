package com.sinapipro.api.report;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.StructuredTaskScope;

/**
 * Structured Concurrency (JEP 480, Java 25) showcase.
 * Collects data from multiple sources in parallel with automatic cancellation on failure.
 */
@Service
public class StructuredReportService {

    private final ReportService reportService;

    public StructuredReportService(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * Generates a managerial report collecting budget, schedule, and cost data concurrently.
     * If any subtask fails, all others are cancelled automatically.
     */
    public byte[] generateManagerialReport(java.util.UUID projectId) throws Exception {
        record ReportData(Map<String, Object> budget, Map<String, Object> schedule, Map<String, Object> costs) {}

        try (var scope = StructuredTaskScope.open()) {
            var budgetTask = scope.fork(() -> collectBudgetData(projectId));
            var scheduleTask = scope.fork(() -> collectScheduleData(projectId));
            var costsTask = scope.fork(() -> collectCostData(projectId));

            scope.join();

            var data = new ReportData(budgetTask.get(), scheduleTask.get(), costsTask.get());

            return reportService.generateWithBaseTemplate(
                    "Relatório Gerencial",
                    buildManagerialHtml(data),
                    Map.of("companyName", "SinapiPRO")
            );
        }
    }

    private Map<String, Object> collectBudgetData(java.util.UUID projectId) {
        // Simulates expensive DB aggregation
        return Map.of("totalBudget", "R$ 2.450.000,00", "items", 342, "stages", 12);
    }

    private Map<String, Object> collectScheduleData(java.util.UUID projectId) {
        return Map.of("progress", "67%", "daysRemaining", 45, "criticalPath", 8);
    }

    private Map<String, Object> collectCostData(java.util.UUID projectId) {
        return Map.of("committed", "R$ 1.890.000,00", "actual", "R$ 1.650.000,00", "cpi", "1.03");
    }

    private String buildManagerialHtml(Object data) {
        return """
            <h2>Resumo Executivo</h2>
            <table>
                <tr><th>Indicador</th><th>Valor</th></tr>
                <tr><td>Orçamento Total</td><td class="right">R$ 2.450.000,00</td></tr>
                <tr><td>Comprometido</td><td class="right">R$ 1.890.000,00</td></tr>
                <tr><td>Realizado</td><td class="right">R$ 1.650.000,00</td></tr>
                <tr><td>CPI</td><td class="right">1.03</td></tr>
                <tr><td>Progresso Físico</td><td class="right">67%</td></tr>
                <tr><td>Dias Restantes</td><td class="right">45</td></tr>
            </table>
            """;
    }
}
