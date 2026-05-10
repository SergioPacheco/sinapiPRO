package com.sinapipro.api.finance.application;

import module java.base;

import com.sinapipro.api.budget.domain.BudgetItem;
import com.sinapipro.api.budget.domain.BudgetItemRepository;
import com.sinapipro.api.budget.domain.BudgetStage;
import com.sinapipro.api.budget.domain.BudgetStageRepository;
import com.sinapipro.api.jobcosting.domain.CostCode;
import com.sinapipro.api.jobcosting.domain.CostCodeRepository;
import com.sinapipro.api.jobcosting.domain.CostTransaction;
import com.sinapipro.api.jobcosting.domain.CostTransactionRepository;
import com.sinapipro.api.jobcosting.domain.CostTransactionType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class BudgetVsActualService {

    private final BudgetStageRepository stageRepository;
    private final BudgetItemRepository itemRepository;
    private final CostCodeRepository costCodeRepository;
    private final CostTransactionRepository transactionRepository;

    public BudgetVsActualService(BudgetStageRepository stageRepository, BudgetItemRepository itemRepository,
                                 CostCodeRepository costCodeRepository, CostTransactionRepository transactionRepository) {
        this.stageRepository = stageRepository;
        this.itemRepository = itemRepository;
        this.costCodeRepository = costCodeRepository;
        this.transactionRepository = transactionRepository;
    }

    /**
     * Consolidated budget vs actual: per cost code with totals.
     */
    public BudgetVsActualReport consolidatedReport(UUID budgetId) {
        var costCodes = costCodeRepository.findByBudgetIdOrderByCode(budgetId);

        var lines = costCodes.stream().map(cc -> {
            var actual = transactionRepository.sumByCodeAndType(cc.getId(), CostTransactionType.ACTUAL);
            var committed = transactionRepository.sumByCodeAndType(cc.getId(), CostTransactionType.COMMITTED);
            var budgeted = cc.getBudgetedAmount();
            var variance = budgeted.subtract(actual).subtract(committed);
            var pctExecuted = budgeted.compareTo(BigDecimal.ZERO) > 0
                    ? actual.multiply(BigDecimal.valueOf(100)).divide(budgeted, 2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;
            return new BudgetVsActualLine(cc.getCode(), cc.getName(), budgeted, committed, actual, variance, pctExecuted);
        }).toList();

        var totalBudgeted = lines.stream().map(BudgetVsActualLine::budgeted).reduce(BigDecimal.ZERO, BigDecimal::add);
        var totalCommitted = lines.stream().map(BudgetVsActualLine::committed).reduce(BigDecimal.ZERO, BigDecimal::add);
        var totalActual = lines.stream().map(BudgetVsActualLine::actual).reduce(BigDecimal.ZERO, BigDecimal::add);
        var totalVariance = totalBudgeted.subtract(totalActual).subtract(totalCommitted);
        var totalPct = totalBudgeted.compareTo(BigDecimal.ZERO) > 0
                ? totalActual.multiply(BigDecimal.valueOf(100)).divide(totalBudgeted, 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return new BudgetVsActualReport(lines,
                new BudgetVsActualLine("TOTAL", "Total Geral", totalBudgeted, totalCommitted, totalActual, totalVariance, totalPct));
    }

    /**
     * Cost by input (composition/service): groups budget items and sums actual costs from transactions.
     */
    public List<CostByInputLine> costByInput(UUID budgetId) {
        var items = itemRepository.findAllByBudgetId(budgetId);

        // Group items by composition
        Map<UUID, List<BudgetItem>> byComposition = items.stream()
                .collect(Collectors.groupingBy(i -> i.getComposition().getId()));

        // Get all actual transactions for this budget
        var transactions = transactionRepository.findByCostCodeBudgetId(budgetId);
        var actualByDescription = transactions.stream()
                .filter(t -> t.getType() == CostTransactionType.ACTUAL)
                .collect(Collectors.groupingBy(
                        t -> t.getDescription() != null ? t.getDescription() : "",
                        Collectors.reducing(BigDecimal.ZERO, CostTransaction::getAmount, BigDecimal::add)));

        return byComposition.entrySet().stream().map(entry -> {
            var itemList = entry.getValue();
            var first = itemList.getFirst();
            var code = first.getComposition().getSinapiCode();
            var description = first.getComposition().getDescription();
            var unit = first.getComposition().getUnit();
            var budgetedQty = itemList.stream().map(BudgetItem::getQuantity).reduce(BigDecimal.ZERO, BigDecimal::add);
            var budgetedCost = itemList.stream().map(BudgetItem::getDirectCost).reduce(BigDecimal.ZERO, BigDecimal::add);
            var unitCost = first.getUnitCost();

            // Match actual costs by description containing the composition code
            var actualCost = actualByDescription.entrySet().stream()
                    .filter(e -> e.getKey().contains(code))
                    .map(Map.Entry::getValue)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            var variance = budgetedCost.subtract(actualCost);
            return new CostByInputLine(code, description, unit, budgetedQty, unitCost, budgetedCost, actualCost, variance);
        }).sorted(Comparator.comparing(CostByInputLine::code)).toList();
    }

    /**
     * Cost by period: groups actual transactions by month.
     */
    public CostByPeriodReport costByPeriod(UUID budgetId) {
        var transactions = transactionRepository.findByCostCodeBudgetId(budgetId);

        Map<String, BigDecimal> actualByMonth = new TreeMap<>();
        Map<String, BigDecimal> committedByMonth = new TreeMap<>();

        for (var tx : transactions) {
            String month = tx.getTransactionDate().withDayOfMonth(1).toString();
            if (tx.getType() == CostTransactionType.ACTUAL) {
                actualByMonth.merge(month, tx.getAmount(), BigDecimal::add);
            } else if (tx.getType() == CostTransactionType.COMMITTED) {
                committedByMonth.merge(month, tx.getAmount(), BigDecimal::add);
            }
        }

        Set<String> allMonths = new TreeSet<>();
        allMonths.addAll(actualByMonth.keySet());
        allMonths.addAll(committedByMonth.keySet());

        List<CostByPeriodLine> lines = new ArrayList<>();
        BigDecimal cumulativeActual = BigDecimal.ZERO;
        for (String month : allMonths) {
            var actual = actualByMonth.getOrDefault(month, BigDecimal.ZERO);
            var committed = committedByMonth.getOrDefault(month, BigDecimal.ZERO);
            cumulativeActual = cumulativeActual.add(actual);
            lines.add(new CostByPeriodLine(month, actual, committed, cumulativeActual));
        }

        var totalBudgeted = costCodeRepository.findByBudgetIdOrderByCode(budgetId).stream()
                .map(CostCode::getBudgetedAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CostByPeriodReport(totalBudgeted, cumulativeActual, totalBudgeted.subtract(cumulativeActual), lines);
    }

    // --- Records ---
    public record BudgetVsActualReport(List<BudgetVsActualLine> lines, BudgetVsActualLine totals) {}
    public record BudgetVsActualLine(String code, String name, BigDecimal budgeted, BigDecimal committed,
                                     BigDecimal actual, BigDecimal variance, BigDecimal pctExecuted) {}

    public record CostByInputLine(String code, String description, String unit, BigDecimal budgetedQty,
                                  BigDecimal unitCost, BigDecimal budgetedCost, BigDecimal actualCost, BigDecimal variance) {}

    public record CostByPeriodReport(BigDecimal totalBudgeted, BigDecimal totalActual, BigDecimal totalVariance,
                                     List<CostByPeriodLine> months) {}
    public record CostByPeriodLine(String month, BigDecimal actual, BigDecimal committed, BigDecimal cumulativeActual) {}
}
