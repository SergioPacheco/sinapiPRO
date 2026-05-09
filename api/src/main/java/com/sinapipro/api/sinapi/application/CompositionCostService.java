package com.sinapipro.api.sinapi.application;

import module java.base;

import com.sinapipro.api.sinapi.domain.*;
import com.sinapipro.api.shared.observability.BusinessObservationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CompositionCostService {

    private final CompositionRepository compositionRepository;
    private final MaterialRepository materialRepository;
    private final BusinessObservationService observationService;

    public CompositionCostService(CompositionRepository compositionRepository,
                                  MaterialRepository materialRepository,
                                  BusinessObservationService observationService) {
        this.compositionRepository = compositionRepository;
        this.materialRepository = materialRepository;
        this.observationService = observationService;
    }

    public CompositionCostResult calculateCost(UUID compositionId, String state, LocalDate referenceMonth) {
        return observationService.observe("sinapi.calculateCost", "sinapi", () -> {
            var composition = compositionRepository.findById(compositionId)
                    .orElseThrow(() -> new IllegalArgumentException("Composition not found: " + compositionId));

            var items = composition.getItems();
            var materialIds = items.stream().map(i -> i.getMaterial().getId()).toList();

            var priceMap = materialRepository.findPricesBatch(materialIds, state, referenceMonth)
                    .stream()
                    .collect(Collectors.toMap(mp -> mp.getMaterial().getId(), MaterialPrice::getPrice));

            var itemCosts = items.stream().map(item -> {
                var price = priceMap.getOrDefault(item.getMaterial().getId(), BigDecimal.ZERO);
                var cost = item.getCoefficient().multiply(price).setScale(4, RoundingMode.HALF_UP);
                return new CompositionCostResult.ItemCost(
                        item.getMaterial().getSinapiCode(),
                        item.getMaterial().getDescription(),
                        item.getMaterial().getUnit(),
                        item.getCoefficient(),
                        price,
                        cost);
            }).toList();

            var totalCost = itemCosts.stream()
                    .map(CompositionCostResult.ItemCost::cost)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            return new CompositionCostResult(
                    composition.getSinapiCode(),
                    composition.getDescription(),
                    composition.getUnit(),
                    state,
                    referenceMonth,
                    totalCost,
                    itemCosts);
        });
    }
}
