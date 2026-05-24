package com.sinapipro.api.analytics.application;

import com.sinapipro.api.budget.domain.BudgetItemRepository;
import com.sinapipro.api.finance.domain.PayableRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class CostMapService {

    private final BudgetItemRepository budgetItemRepository;
    private final PayableRepository payableRepository;

    public CostMapService(BudgetItemRepository budgetItemRepository, PayableRepository payableRepository) {
        this.budgetItemRepository = budgetItemRepository;
        this.payableRepository = payableRepository;
    }

    /**
     * Mapa de custos: orçado × comprometido × realizado × desvio.
     */
    public CostMap generate(UUID budgetId) {
        var budgeted = budgetItemRepository.sumDirectCostByBudget(budgetId);
        var committed = payableRepository.sumPendingByBudget(budgetId);
        var realized = payableRepository.sumPaidByBudget(budgetId);
        var totalCommitted = committed.add(realized);
        var deviation = totalCommitted.subtract(budgeted);
        var deviationPct = budgeted.signum() > 0
                ? deviation.multiply(BigDecimal.valueOf(100)).divide(budgeted, 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        var balance = budgeted.subtract(totalCommitted);

        return new CostMap(budgeted, committed, realized, totalCommitted, balance, deviation, deviationPct);
    }

    public record CostMap(BigDecimal budgeted, BigDecimal committed, BigDecimal realized,
                           BigDecimal totalCommitted, BigDecimal balance,
                           BigDecimal deviation, BigDecimal deviationPct) {}
}
