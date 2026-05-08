package com.sinapipro.api.budget.application;

import com.sinapipro.api.budget.domain.BudgetItem;
import com.sinapipro.api.budget.domain.BudgetItemRepository;
import com.sinapipro.api.sinapi.domain.CompositionItem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AbcCurveService {

    private final BudgetItemRepository itemRepository;

    public AbcCurveService(BudgetItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public List<AbcEntry> calculateAbcCurve(UUID budgetId) {
        List<BudgetItem> budgetItems = itemRepository.findAllByBudgetId(budgetId);

        // Aggregate cost by material across all compositions
        Map<String, MaterialAccumulator> materialCosts = new HashMap<>();

        for (BudgetItem bi : budgetItems) {
            for (CompositionItem ci : bi.getComposition().getItems()) {
                String key = ci.getMaterial().getSinapiCode();
                materialCosts.computeIfAbsent(key, k -> new MaterialAccumulator(
                        ci.getMaterial().getSinapiCode(),
                        ci.getMaterial().getDescription(),
                        ci.getMaterial().getUnit()
                )).addCost(bi.getQuantity().multiply(ci.getCoefficient()).multiply(bi.getUnitCost()));
            }
        }

        BigDecimal totalCost = materialCosts.values().stream()
                .map(MaterialAccumulator::totalCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalCost.compareTo(BigDecimal.ZERO) == 0) return List.of();

        // Sort descending by cost, calculate cumulative %
        List<MaterialAccumulator> sorted = materialCosts.values().stream()
                .sorted(Comparator.comparing(MaterialAccumulator::totalCost).reversed())
                .toList();

        List<AbcEntry> result = new ArrayList<>();
        BigDecimal cumulative = BigDecimal.ZERO;

        for (MaterialAccumulator ma : sorted) {
            BigDecimal pct = ma.totalCost().multiply(BigDecimal.valueOf(100)).divide(totalCost, 2, RoundingMode.HALF_UP);
            cumulative = cumulative.add(pct);
            String classification = cumulative.compareTo(BigDecimal.valueOf(80)) <= 0 ? "A"
                    : cumulative.compareTo(BigDecimal.valueOf(95)) <= 0 ? "B" : "C";
            result.add(new AbcEntry(ma.code, ma.description, ma.unit, ma.totalCost(), pct, cumulative, classification));
        }
        return result;
    }

    public record AbcEntry(String materialCode, String description, String unit,
                           BigDecimal cost, BigDecimal percentage, BigDecimal cumulativePercentage, String classification) {}

    private static class MaterialAccumulator {
        final String code;
        final String description;
        final String unit;
        private BigDecimal cost = BigDecimal.ZERO;

        MaterialAccumulator(String code, String description, String unit) {
            this.code = code;
            this.description = description;
            this.unit = unit;
        }

        void addCost(BigDecimal amount) { cost = cost.add(amount); }
        BigDecimal totalCost() { return cost; }
    }
}
