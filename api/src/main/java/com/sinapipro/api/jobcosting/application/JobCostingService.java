package com.sinapipro.api.jobcosting.application;

import module java.base;

import com.sinapipro.api.jobcosting.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class JobCostingService {

    private final CostCodeRepository codeRepository;
    private final CostTransactionRepository transactionRepository;

    public JobCostingService(CostCodeRepository codeRepository, CostTransactionRepository transactionRepository) {
        this.codeRepository = codeRepository;
        this.transactionRepository = transactionRepository;
    }

    public CostCodeSummary summarize(UUID costCodeId) {
        var code = codeRepository.findById(costCodeId).orElseThrow();
        var actual = transactionRepository.sumByCodeAndType(costCodeId, CostTransactionType.ACTUAL);
        var committed = transactionRepository.sumByCodeAndType(costCodeId, CostTransactionType.COMMITTED);
        var budgeted = code.getBudgetedAmount();
        var variance = budgeted.subtract(actual).subtract(committed);
        return new CostCodeSummary(code.getCode(), code.getName(), budgeted, actual, committed, variance);
    }

    public List<CostCodeSummary> summarizeAll(UUID budgetId) {
        return codeRepository.findByBudgetIdOrderByCode(budgetId).stream()
                .map(code -> summarize(code.getId()))
                .toList();
    }

    public BudgetCostSummary budgetSummary(UUID budgetId) {
        var codes = codeRepository.findByBudgetIdOrderByCode(budgetId);
        var totalBudgeted = codes.stream().map(CostCode::getBudgetedAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        var totalActual = transactionRepository.sumByBudgetAndType(budgetId, CostTransactionType.ACTUAL);
        var totalCommitted = transactionRepository.sumByBudgetAndType(budgetId, CostTransactionType.COMMITTED);
        var totalVariance = totalBudgeted.subtract(totalActual).subtract(totalCommitted);
        return new BudgetCostSummary(totalBudgeted, totalActual, totalCommitted, totalVariance);
    }

    public record CostCodeSummary(String code, String name, BigDecimal budgeted, BigDecimal actual,
                                  BigDecimal committed, BigDecimal availableBalance) {}

    public record BudgetCostSummary(BigDecimal totalBudgeted, BigDecimal totalActual,
                                    BigDecimal totalCommitted, BigDecimal totalVariance) {}
}
