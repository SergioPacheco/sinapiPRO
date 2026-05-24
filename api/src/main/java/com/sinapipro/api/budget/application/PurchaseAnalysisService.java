package com.sinapipro.api.budget.application;

import com.sinapipro.api.budget.domain.*;
import com.sinapipro.api.finance.domain.PayableRepository;
import com.sinapipro.api.shared.error.DomainNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class PurchaseAnalysisService {

    private final BudgetRepository budgetRepository;
    private final BudgetItemRepository itemRepository;
    private final PayableRepository payableRepository;

    public PurchaseAnalysisService(BudgetRepository budgetRepository,
                                    BudgetItemRepository itemRepository,
                                    PayableRepository payableRepository) {
        this.budgetRepository = budgetRepository;
        this.itemRepository = itemRepository;
        this.payableRepository = payableRepository;
    }

    /**
     * Análise de compras: orçado × comprometido × realizado × saldo.
     */
    public PurchaseAnalysis analyze(UUID budgetId) {
        budgetRepository.findById(budgetId)
                .orElseThrow(() -> new DomainNotFoundException("Budget not found: " + budgetId));

        var budgeted = itemRepository.sumDirectCostByBudget(budgetId);
        var committed = payableRepository.sumPendingByBudget(budgetId);
        var realized = payableRepository.sumPaidByBudget(budgetId);
        var balance = budgeted.subtract(committed).subtract(realized);

        var commitPct = budgeted.signum() > 0
                ? committed.add(realized).multiply(BigDecimal.valueOf(100))
                    .divide(budgeted, 2, java.math.RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return new PurchaseAnalysis(budgeted, committed, realized, balance, commitPct);
    }

    public record PurchaseAnalysis(BigDecimal budgeted, BigDecimal committed, BigDecimal realized,
                                    BigDecimal balance, BigDecimal commitPercentage) {}
}
