package com.sinapipro.api.sinapi.application;

import com.sinapipro.api.sinapi.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ItemSearchService {

    private final MaterialRepository materialRepository;
    private final CompositionRepository compositionRepository;

    public ItemSearchService(MaterialRepository materialRepository, CompositionRepository compositionRepository) {
        this.materialRepository = materialRepository;
        this.compositionRepository = compositionRepository;
    }

    public List<ItemSearchResult> search(String query, ItemType typeFilter) {
        if (query == null || query.length() < 3) return List.of();

        List<ItemSearchResult> results = new ArrayList<>();

        if (typeFilter == null || typeFilter != ItemType.COMPOSITION) {
            var materials = materialRepository.searchByText(query, 20);
            results.addAll(materials.stream()
                    .map(m -> new ItemSearchResult(m.getId(), m.getSinapiCode(), m.getDescription(), m.getUnit(), ItemType.MATERIAL, null))
                    .toList());
        }

        if (typeFilter == null || typeFilter == ItemType.COMPOSITION) {
            var compositions = compositionRepository.searchCurrentByText(query, 20);
            results.addAll(compositions.stream()
                    .map(c -> new ItemSearchResult(c.getId(), c.getSinapiCode(), c.getDescription(), c.getUnit(), ItemType.COMPOSITION, null))
                    .toList());
        }

        return results.stream().limit(20).toList();
    }

    public record ItemSearchResult(UUID id, String code, String description, String unit, ItemType type, BigDecimal latestPrice) {}
}
