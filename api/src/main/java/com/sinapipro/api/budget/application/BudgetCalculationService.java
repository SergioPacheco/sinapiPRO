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

    private final BudgetItemRepository itemRepository;
    private final BdiConfigRepository bdiConfigRepository;

    public BudgetCalculationService(BudgetItemRepository itemRepository, BdiConfigRepository bdiConfigRepository) {
        this.itemRepository = itemRepository;
        this.bdiConfigRepository = bdiConfigRepository;
    }

    public BudgetSummary calculateSummary(UUID budgetId) {
        BigDecimal directCost = itemRepository.sumDirectCostByBudget(budgetId);
        BigDecimal bdiPct = bdiConfigRepository.findByBudgetIdAndItemType(budgetId, DEFAULT_BDI_ITEM_TYPE)
                .map(BdiConfig::getTotalBdi)
                .orElse(BigDecimal.ZERO);
        BigDecimal bdiAmount = directCost.multiply(bdiPct).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalWithBdi = directCost.add(bdiAmount);

        return new BudgetSummary(directCost, bdiPct, bdiAmount, totalWithBdi);
    }

    public record BudgetSummary(BigDecimal directCost, BigDecimal bdiPct, BigDecimal bdiAmount, BigDecimal totalWithBdi) {}
}
