package com.sinapipro.api.jobcosting.application;

import com.sinapipro.api.jobcosting.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

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
        CostCode code = codeRepository.findById(costCodeId).orElseThrow();
        BigDecimal actual = transactionRepository.sumByCodeAndType(costCodeId, CostTransactionType.ACTUAL);
        BigDecimal committed = transactionRepository.sumByCodeAndType(costCodeId, CostTransactionType.COMMITTED);
        BigDecimal budgeted = code.getBudgetedAmount();
        BigDecimal variance = budgeted.subtract(actual).subtract(committed);
        return new CostCodeSummary(code.getCode(), code.getName(), budgeted, actual, committed, variance);
    }

    public List<CostCodeSummary> summarizeAll(UUID budgetId) {
        return codeRepository.findByBudgetIdOrderByCode(budgetId).stream()
                .map(code -> summarize(code.getId()))
                .toList();
    }

    public BudgetCostSummary budgetSummary(UUID budgetId) {
        List<CostCode> codes = codeRepository.findByBudgetIdOrderByCode(budgetId);
        BigDecimal totalBudgeted = codes.stream().map(CostCode::getBudgetedAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalActual = transactionRepository.sumByBudgetAndType(budgetId, CostTransactionType.ACTUAL);
        BigDecimal totalCommitted = transactionRepository.sumByBudgetAndType(budgetId, CostTransactionType.COMMITTED);
        BigDecimal totalVariance = totalBudgeted.subtract(totalActual).subtract(totalCommitted);
        return new BudgetCostSummary(totalBudgeted, totalActual, totalCommitted, totalVariance);
    }

    public record CostCodeSummary(String code, String name, BigDecimal budgeted, BigDecimal actual,
                                  BigDecimal committed, BigDecimal availableBalance) {}

    public record BudgetCostSummary(BigDecimal totalBudgeted, BigDecimal totalActual,
                                    BigDecimal totalCommitted, BigDecimal totalVariance) {}
}
