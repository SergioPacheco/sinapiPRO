package com.sinapipro.api.budget.application;

import com.sinapipro.api.budget.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class BudgetCalculationService {
    private static final String DEFAULT_BDI_ITEM_TYPE = "ALL";

    private final BudgetRepository budgetRepository;
    private final BudgetItemRepository itemRepository;
    private final BdiConfigRepository bdiConfigRepository;

    public BudgetCalculationService(BudgetRepository budgetRepository, BudgetItemRepository itemRepository,
                                    BdiConfigRepository bdiConfigRepository) {
        this.budgetRepository = budgetRepository;
        this.itemRepository = itemRepository;
        this.bdiConfigRepository = bdiConfigRepository;
    }

    public BudgetSummary calculateSummary(UUID budgetId) {
        var budget = budgetRepository.findById(budgetId).orElse(null);
        String method = budget != null ? budget.getRoundingMethod() : "TRUNCATE";
        int decimals = budget != null && budget.getDecimalPlaces() != null ? budget.getDecimalPlaces() : 4;

        BigDecimal directCost = itemRepository.sumDirectCostByBudget(budgetId);
        BigDecimal bdiPct = bdiConfigRepository.findByBudgetIdAndItemType(budgetId, DEFAULT_BDI_ITEM_TYPE)
                .map(BdiConfig::getTotalBdi)
                .orElse(BigDecimal.ZERO);
        BigDecimal bdiAmount = RoundingUtil.apply(directCost.multiply(bdiPct), method, decimals);
        BigDecimal totalWithBdi = RoundingUtil.apply(directCost.add(bdiAmount), method, decimals);

        return new BudgetSummary(directCost, bdiPct, bdiAmount, totalWithBdi);
    }

    public record BudgetSummary(BigDecimal directCost, BigDecimal bdiPct, BigDecimal bdiAmount, BigDecimal totalWithBdi) {}
}
