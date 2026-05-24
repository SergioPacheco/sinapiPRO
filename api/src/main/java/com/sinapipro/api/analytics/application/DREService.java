package com.sinapipro.api.analytics.application;

import com.sinapipro.api.budget.domain.BudgetItemRepository;
import com.sinapipro.api.budget.domain.BudgetRepository;
import com.sinapipro.api.finance.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

@Service
@Transactional(readOnly = true)
public class DREService {

    private final PayableRepository payableRepository;
    private final ReceivableRepository receivableRepository;
    private final BudgetItemRepository budgetItemRepository;

    public DREService(PayableRepository payableRepository, ReceivableRepository receivableRepository,
                      BudgetItemRepository budgetItemRepository) {
        this.payableRepository = payableRepository;
        this.receivableRepository = receivableRepository;
        this.budgetItemRepository = budgetItemRepository;
    }

    /**
     * DRE por obra (budget): Receitas - Custos Diretos - Despesas Indiretas = Resultado.
     */
    public DRE generate(UUID budgetId, LocalDate from, LocalDate to) {
        var receivables = receivableRepository.findByBudgetIdAndDueDateBetween(budgetId, from, to);
        var payables = payableRepository.findByBudgetIdAndDueDateBetween(budgetId, from, to);

        var grossRevenue = receivables.stream()
                .map(r -> r.getReceivedAmount() != null ? r.getReceivedAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        var directCosts = payables.stream()
                .filter(p -> "MATERIAL".equals(p.getCategory()) || "MO".equals(p.getCategory()) || "EQUIPMENT".equals(p.getCategory()))
                .map(p -> p.getPaidAmount() != null ? p.getPaidAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        var indirectCosts = payables.stream()
                .filter(p -> !"MATERIAL".equals(p.getCategory()) && !"MO".equals(p.getCategory()) && !"EQUIPMENT".equals(p.getCategory()))
                .map(p -> p.getPaidAmount() != null ? p.getPaidAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        var grossProfit = grossRevenue.subtract(directCosts);
        var netResult = grossProfit.subtract(indirectCosts);
        var margin = grossRevenue.signum() > 0
                ? netResult.multiply(BigDecimal.valueOf(100)).divide(grossRevenue, 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return new DRE(grossRevenue, directCosts, grossProfit, indirectCosts, netResult, margin);
    }

    public record DRE(BigDecimal grossRevenue, BigDecimal directCosts, BigDecimal grossProfit,
                       BigDecimal indirectCosts, BigDecimal netResult, BigDecimal marginPct) {}
}
