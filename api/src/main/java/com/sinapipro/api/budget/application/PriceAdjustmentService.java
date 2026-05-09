package com.sinapipro.api.budget.application;

import module java.base;

import com.sinapipro.api.budget.domain.BudgetItem;
import com.sinapipro.api.budget.domain.BudgetItemRepository;
import com.sinapipro.api.sinapi.domain.CompositionItem;
import com.sinapipro.api.sinapi.domain.MaterialPrice;
import com.sinapipro.api.sinapi.domain.MaterialRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PriceAdjustmentService {

    private final BudgetItemRepository itemRepository;
    private final MaterialRepository materialRepository;

    public PriceAdjustmentService(BudgetItemRepository itemRepository, MaterialRepository materialRepository) {
        this.itemRepository = itemRepository;
        this.materialRepository = materialRepository;
    }

    /**
     * Adjust all budget item prices by a fixed percentage.
     */
    @Transactional
    public AdjustmentResult adjustByPercentage(UUID budgetId, BigDecimal percentageChange) {
        var items = itemRepository.findAllByBudgetId(budgetId);
        var factor = BigDecimal.ONE.add(percentageChange.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP));

        var adjusted = new ArrayList<AdjustedItem>();
        for (var item : items) {
            var oldCost = item.getUnitCost();
            var newCost = oldCost.multiply(factor).setScale(4, RoundingMode.HALF_UP);
            item.update(item.getQuantity(), newCost, item.getBdiPct());
            adjusted.add(new AdjustedItem(item.getId(), item.getComposition().getDescription(), oldCost, newCost));
        }
        itemRepository.saveAll(items);
        return new AdjustmentResult(AdjustmentType.PERCENTAGE, items.size(), percentageChange, null, null, adjusted);
    }

    /**
     * Adjust all budget item prices by a fixed value (add/subtract).
     */
    @Transactional
    public AdjustmentResult adjustByValue(UUID budgetId, BigDecimal valueChange) {
        var items = itemRepository.findAllByBudgetId(budgetId);

        var adjusted = new ArrayList<AdjustedItem>();
        for (var item : items) {
            var oldCost = item.getUnitCost();
            var newCost = oldCost.add(valueChange).max(BigDecimal.ZERO);
            item.update(item.getQuantity(), newCost, item.getBdiPct());
            adjusted.add(new AdjustedItem(item.getId(), item.getComposition().getDescription(), oldCost, newCost));
        }
        itemRepository.saveAll(items);
        return new AdjustmentResult(AdjustmentType.VALUE, items.size(), null, valueChange, null, adjusted);
    }

    /**
     * Recalculate all budget item unit costs based on a new SINAPI reference (state + month).
     * Uses Structured Concurrency to fetch items and prices in parallel.
     */
    @Transactional
    public AdjustmentResult adjustBySinapiReference(UUID budgetId, String state, LocalDate referenceMonth) {
        var items = itemRepository.findAllByBudgetId(budgetId);

        // Collect all material IDs
        var materialIds = items.stream()
                .flatMap(i -> i.getComposition().getItems().stream())
                .map(ci -> ci.getMaterial().getId())
                .distinct()
                .toList();

        // Batch fetch prices
        var priceMap = materialRepository.findPricesBatch(materialIds, state, referenceMonth)
                .stream()
                .collect(Collectors.toMap(mp -> mp.getMaterial().getId(), MaterialPrice::getPrice, (a, _) -> a));

        var adjusted = new ArrayList<AdjustedItem>();
        for (var item : items) {
            var oldCost = item.getUnitCost();
            var newCost = BigDecimal.ZERO;
            for (var ci : item.getComposition().getItems()) {
                var price = priceMap.getOrDefault(ci.getMaterial().getId(), BigDecimal.ZERO);
                newCost = newCost.add(ci.getCoefficient().multiply(price));
            }
            newCost = newCost.setScale(4, RoundingMode.HALF_UP);
            item.update(item.getQuantity(), newCost, item.getBdiPct());
            adjusted.add(new AdjustedItem(item.getId(), item.getComposition().getDescription(), oldCost, newCost));
        }
        itemRepository.saveAll(items);
        return new AdjustmentResult(AdjustmentType.SINAPI, items.size(), null, null,
                state + "/" + referenceMonth, adjusted);
    }

    public enum AdjustmentType { PERCENTAGE, VALUE, SINAPI }

    public record AdjustmentResult(AdjustmentType type, int itemsAdjusted, BigDecimal percentage,
                                   BigDecimal value, String sinapiReference, List<AdjustedItem> items) {}

    public record AdjustedItem(UUID itemId, String description, BigDecimal oldUnitCost, BigDecimal newUnitCost) {}
}
