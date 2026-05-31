package com.sinapipro.api.sinapi.api;

import com.sinapipro.api.sinapi.domain.Composition;
import com.sinapipro.api.sinapi.domain.CompositionItem;
import com.sinapipro.api.sinapi.domain.ItemType;
import com.sinapipro.api.sinapi.domain.MaterialPrice;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public record CompositionResponse(
        UUID id,
        String sinapiCode,
        String description,
        String unit,
        String groupName,
        String origin,
        boolean editable,
        Integer version,
        UUID parentId,
        Boolean isCurrent,
        BigDecimal unitCost,
        List<ItemResponse> items,
        Instant createdAt
) {
    public static CompositionResponse from(Composition c) {
        return new CompositionResponse(c.getId(), c.getSinapiCode(), c.getDescription(),
                c.getUnit(), c.getGroupName(), c.getOrigin(), c.isEditable(),
                c.getVersion(), c.getParentId(), c.getIsCurrent(),
                c.getUnitCost(),
                null, c.getCreatedAt());
    }

    public static CompositionResponse fromWithItems(Composition c) {
        return new CompositionResponse(c.getId(), c.getSinapiCode(), c.getDescription(),
                c.getUnit(), c.getGroupName(), c.getOrigin(), c.isEditable(),
                c.getVersion(), c.getParentId(), c.getIsCurrent(),
                c.getUnitCost(),
                c.getItems().stream().map(ItemResponse::from).toList(),
                c.getCreatedAt());
    }

    public record ItemResponse(
            UUID id,
            String type,
            String code,
            String description,
            String unit,
            BigDecimal coefficient,
            BigDecimal latestPrice
    ) {
        public static ItemResponse from(CompositionItem i) {
            if (i.getItemType() == ItemType.COMPOSITION) {
                var child = i.getChildComposition();
                return new ItemResponse(i.getId(), i.getItemType().name(),
                        child.getSinapiCode(), child.getDescription(), child.getUnit(),
                        i.getCoefficient(), child.getUnitCost());
            } else {
                var mat = i.getMaterial();
                BigDecimal price = mat.getPrices().stream()
                        .max(Comparator.comparing(MaterialPrice::getReferenceMonth))
                        .map(MaterialPrice::getPrice)
                        .orElse(null);
                return new ItemResponse(i.getId(), i.getItemType().name(),
                        mat.getSinapiCode(), mat.getDescription(), mat.getUnit(),
                        i.getCoefficient(), price);
            }
        }
    }
}
