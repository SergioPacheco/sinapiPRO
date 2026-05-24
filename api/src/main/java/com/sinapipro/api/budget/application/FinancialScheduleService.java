package com.sinapipro.api.budget.application;

import com.sinapipro.api.budget.domain.*;
import com.sinapipro.api.shared.error.DomainNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

@Service
@Transactional(readOnly = true)
public class FinancialScheduleService {

    private final BudgetRepository budgetRepository;
    private final BudgetItemRepository itemRepository;

    public FinancialScheduleService(BudgetRepository budgetRepository, BudgetItemRepository itemRepository) {
        this.budgetRepository = budgetRepository;
        this.itemRepository = itemRepository;
    }

    /**
     * Distribui o valor total do orçamento por mês (curva S financeira).
     * Distribuição linear entre startDate e endDate.
     */
    public List<MonthlyAmount> generateSchedule(UUID budgetId) {
        var budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new DomainNotFoundException("Budget not found: " + budgetId));

        var totalCost = itemRepository.sumDirectCostByBudget(budgetId);
        if (totalCost == null || totalCost.signum() == 0) {
            return List.of();
        }

        var start = budget.getStartDate();
        var end = budget.getEndDate() != null ? budget.getEndDate() : start.plusMonths(12);

        var startMonth = YearMonth.from(start);
        var endMonth = YearMonth.from(end);
        var months = new ArrayList<YearMonth>();
        var current = startMonth;
        while (!current.isAfter(endMonth)) {
            months.add(current);
            current = current.plusMonths(1);
        }

        if (months.isEmpty()) return List.of();

        // Distribuição linear
        var monthlyAmount = totalCost.divide(BigDecimal.valueOf(months.size()), 2, RoundingMode.FLOOR);
        var remainder = totalCost.subtract(monthlyAmount.multiply(BigDecimal.valueOf(months.size())));

        var schedule = new ArrayList<MonthlyAmount>();
        var accumulated = BigDecimal.ZERO;
        for (int i = 0; i < months.size(); i++) {
            var amount = (i == months.size() - 1) ? monthlyAmount.add(remainder) : monthlyAmount;
            accumulated = accumulated.add(amount);
            var pct = accumulated.multiply(BigDecimal.valueOf(100)).divide(totalCost, 2, RoundingMode.HALF_UP);
            schedule.add(new MonthlyAmount(months.get(i), amount, accumulated, pct));
        }

        return schedule;
    }

    /**
     * Distribui com pesos customizados por mês (ex: curva S real).
     */
    public List<MonthlyAmount> generateSchedule(UUID budgetId, List<BigDecimal> weights) {
        var budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new DomainNotFoundException("Budget not found: " + budgetId));

        var totalCost = itemRepository.sumDirectCostByBudget(budgetId);
        if (totalCost == null || totalCost.signum() == 0) return List.of();

        var start = YearMonth.from(budget.getStartDate());
        var totalWeight = weights.stream().reduce(BigDecimal.ZERO, BigDecimal::add);

        var schedule = new ArrayList<MonthlyAmount>();
        var accumulated = BigDecimal.ZERO;
        for (int i = 0; i < weights.size(); i++) {
            var amount = totalCost.multiply(weights.get(i))
                    .divide(totalWeight, 2, RoundingMode.HALF_UP);
            accumulated = accumulated.add(amount);
            var pct = accumulated.multiply(BigDecimal.valueOf(100)).divide(totalCost, 2, RoundingMode.HALF_UP);
            schedule.add(new MonthlyAmount(start.plusMonths(i), amount, accumulated, pct));
        }

        return schedule;
    }

    public record MonthlyAmount(YearMonth month, BigDecimal amount, BigDecimal accumulated, BigDecimal percentComplete) {}
}
