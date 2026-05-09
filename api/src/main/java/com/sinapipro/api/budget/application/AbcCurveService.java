package com.sinapipro.api.budget.application;

import module java.base;

import com.sinapipro.api.budget.domain.BudgetItem;
import com.sinapipro.api.budget.domain.BudgetItemRepository;
import com.sinapipro.api.sinapi.domain.CompositionItem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AbcCurveService {

    private final BudgetItemRepository itemRepository;

    public AbcCurveService(BudgetItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public List<AbcEntry> calculateAbcCurve(UUID budgetId) {
        var budgetItems = itemRepository.findAllByBudgetId(budgetId);

        // Aggregate cost by material across all compositions
        var materialCosts = new HashMap<String, MaterialAccumulator>();

        for (var bi : budgetItems) {
            for (var ci : bi.getComposition().getItems()) {
                var key = ci.getMaterial().getSinapiCode();
                materialCosts.computeIfAbsent(key, _ -> new MaterialAccumulator(
                        ci.getMaterial().getSinapiCode(),
                        ci.getMaterial().getDescription(),
                        ci.getMaterial().getUnit()
                )).addCost(bi.getQuantity().multiply(ci.getCoefficient()).multiply(bi.getUnitCost()));
            }
        }

        var totalCost = materialCosts.values().stream()
                .map(MaterialAccumulator::totalCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalCost.compareTo(BigDecimal.ZERO) == 0) return List.of();

        // Sort descending by cost, calculate cumulative % using Gatherers.fold
        var sorted = materialCosts.values().stream()
                .sorted(Comparator.comparing(MaterialAccumulator::totalCost).reversed())
                .toList();

        var result = new ArrayList<AbcEntry>();
        var cumulative = BigDecimal.ZERO;

        for (var ma : sorted) {
            var pct = ma.totalCost().multiply(BigDecimal.valueOf(100)).divide(totalCost, 2, RoundingMode.HALF_UP);
            cumulative = cumulative.add(pct);
            var classification = cumulative.compareTo(BigDecimal.valueOf(80)) <= 0 ? "A"
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
