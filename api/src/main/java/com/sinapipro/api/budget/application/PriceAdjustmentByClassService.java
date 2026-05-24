package com.sinapipro.api.budget.application;

import com.sinapipro.api.budget.domain.*;
import com.sinapipro.api.shared.error.DomainNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class PriceAdjustmentByClassService {

    private final BudgetRepository budgetRepository;
    private final BudgetItemRepository itemRepository;

    public PriceAdjustmentByClassService(BudgetRepository budgetRepository,
                                          BudgetItemRepository itemRepository) {
        this.budgetRepository = budgetRepository;
        this.itemRepository = itemRepository;
    }

    /**
     * Aplica reajuste percentual por tipo de item (classe da composição).
     * Ex: {"MATERIAL": 5.0, "MAO_DE_OBRA": 8.0, "EQUIPAMENTO": 3.0}
     */
    public AdjustmentResult adjustByClass(UUID budgetId, Map<String, BigDecimal> adjustmentsByClass) {
        var budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new DomainNotFoundException("Budget not found: " + budgetId));

        if (budget.getStatus() == BudgetStatus.IN_EXECUTION) {
            throw new IllegalStateException("Cannot adjust prices of an effectuated budget");
        }

        var items = itemRepository.findAllByBudgetId(budgetId);
        int adjusted = 0;

        for (var item : items) {
            var itemClass = item.getPriceSource() != null ? item.getPriceSource() : "SINAPI";
            var pct = adjustmentsByClass.get(itemClass);
            if (pct == null) {
                // Tentar pelo groupName da composição
                var group = item.getComposition().getGroupName();
                if (group != null) pct = adjustmentsByClass.get(group);
            }
            if (pct != null && pct.signum() != 0) {
                var factor = BigDecimal.ONE.add(pct.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP));
                var newCost = item.getUnitCost().multiply(factor).setScale(4, RoundingMode.HALF_UP);
                item.setUnitCost(newCost);
                adjusted++;
            }
        }

        itemRepository.saveAll(items);
        return new AdjustmentResult(adjusted, items.size());
    }

    public record AdjustmentResult(int adjustedItems, int totalItems) {}
}
