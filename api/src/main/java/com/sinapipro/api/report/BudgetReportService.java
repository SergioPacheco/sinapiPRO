package com.sinapipro.api.report;

import com.sinapipro.api.budget.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class BudgetReportService {

    private final ReportService reportService;
    private final BudgetRepository budgetRepo;
    private final BudgetItemRepository itemRepo;

    public BudgetReportService(ReportService reportService, BudgetRepository budgetRepo, BudgetItemRepository itemRepo) {
        this.reportService = reportService; this.budgetRepo = budgetRepo; this.itemRepo = itemRepo;
    }

    public byte[] analitico(UUID id) { return reportService.generatePdf("reports/budget/analitico.jte", Map.of("budget", budgetRepo.findById(id).orElseThrow(), "items", itemRepo.findAllByBudgetId(id))); }
    public byte[] sintetico(UUID id) {
        var budget = budgetRepo.findById(id).orElseThrow();
        var items = itemRepo.findAllByBudgetId(id);
        var total = items.stream().map(BudgetItem::getDirectCost).reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        return reportService.generatePdf("reports/budget/sintetico.jte", Map.of("budget", budget, "items", items, "total", total));
    }
    public byte[] cpu(UUID id) { return reportService.generatePdf("reports/budget/cpu.jte", Map.of("items", itemRepo.findAllByBudgetId(id))); }
    public byte[] cronogramaFinanceiro(UUID id) { return reportService.generatePdf("reports/budget/cronograma-financeiro.jte", Map.of("budgetId", id)); }
    public byte[] analiseCompras(UUID id) { return reportService.generatePdf("reports/budget/analise-compras.jte", Map.of("budgetId", id)); }
    public byte[] comparativo(UUID id1, UUID id2) { return reportService.generatePdf("reports/budget/comparativo.jte", Map.of("budgetId1", id1, "budgetId2", id2)); }
    public byte[] listagemInsumos(UUID id) { return reportService.generatePdf("reports/budget/listagem-insumos.jte", Map.of("items", itemRepo.findAllByBudgetId(id))); }
    public byte[] bdiDetalhado(UUID id) { return reportService.generatePdf("reports/budget/bdi-detalhado.jte", Map.of("budgetId", id)); }
}
