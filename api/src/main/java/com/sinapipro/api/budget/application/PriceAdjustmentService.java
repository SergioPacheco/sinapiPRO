package com.sinapipro.api.budget.application;

import com.sinapipro.api.budget.domain.BudgetItem;
import com.sinapipro.api.budget.domain.BudgetItemRepository;
import com.sinapipro.api.sinapi.domain.CompositionItem;
import com.sinapipro.api.sinapi.domain.MaterialPrice;
import com.sinapipro.api.sinapi.domain.MaterialRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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
        List<BudgetItem> items = itemRepository.findAllByBudgetId(budgetId);
        BigDecimal factor = BigDecimal.ONE.add(percentageChange.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP));

        List<AdjustedItem> adjusted = new ArrayList<>();
        for (BudgetItem item : items) {
            BigDecimal oldCost = item.getUnitCost();
            BigDecimal newCost = oldCost.multiply(factor).setScale(4, RoundingMode.HALF_UP);
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
        List<BudgetItem> items = itemRepository.findAllByBudgetId(budgetId);

        List<AdjustedItem> adjusted = new ArrayList<>();
        for (BudgetItem item : items) {
            BigDecimal oldCost = item.getUnitCost();
            BigDecimal newCost = oldCost.add(valueChange).max(BigDecimal.ZERO);
            item.update(item.getQuantity(), newCost, item.getBdiPct());
            adjusted.add(new AdjustedItem(item.getId(), item.getComposition().getDescription(), oldCost, newCost));
        }
        itemRepository.saveAll(items);
        return new AdjustmentResult(AdjustmentType.VALUE, items.size(), null, valueChange, null, adjusted);
    }

    /**
     * Recalculate all budget item unit costs based on a new SINAPI reference (state + month).
     * Each item's unit cost = sum(coefficient × material price) for its composition.
     */
    @Transactional
    public AdjustmentResult adjustBySinapiReference(UUID budgetId, String state, LocalDate referenceMonth) {
        List<BudgetItem> items = itemRepository.findAllByBudgetId(budgetId);

        // Collect all material IDs
        List<UUID> materialIds = items.stream()
                .flatMap(i -> i.getComposition().getItems().stream())
                .map(ci -> ci.getMaterial().getId())
                .distinct()
                .toList();

        // Batch fetch prices
        Map<UUID, BigDecimal> priceMap = materialRepository.findPricesBatch(materialIds, state, referenceMonth)
                .stream()
                .collect(Collectors.toMap(mp -> mp.getMaterial().getId(), MaterialPrice::getPrice, (a, b) -> a));

        List<AdjustedItem> adjusted = new ArrayList<>();
        for (BudgetItem item : items) {
            BigDecimal oldCost = item.getUnitCost();
            BigDecimal newCost = BigDecimal.ZERO;
            for (CompositionItem ci : item.getComposition().getItems()) {
                BigDecimal price = priceMap.getOrDefault(ci.getMaterial().getId(), BigDecimal.ZERO);
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
