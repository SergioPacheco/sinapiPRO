package com.sinapipro.api.sinapi.api;

import com.sinapipro.api.sinapi.domain.Composition;
import com.sinapipro.api.sinapi.domain.CompositionItem;
import com.sinapipro.api.sinapi.domain.ItemType;

import java.math.BigDecimal;
import java.time.Instant;
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
        List<ItemResponse> items,
        Instant createdAt
) {
    public static CompositionResponse from(Composition c) {
        return new CompositionResponse(c.getId(), c.getSinapiCode(), c.getDescription(),
                c.getUnit(), c.getGroupName(), c.getOrigin(), c.isEditable(),
                c.getVersion(), c.getParentId(), c.getIsCurrent(),
                null, c.getCreatedAt());
    }

    public static CompositionResponse fromWithItems(Composition c) {
        return new CompositionResponse(c.getId(), c.getSinapiCode(), c.getDescription(),
                c.getUnit(), c.getGroupName(), c.getOrigin(), c.isEditable(),
                c.getVersion(), c.getParentId(), c.getIsCurrent(),
                c.getItems().stream().map(ItemResponse::from).toList(),
                c.getCreatedAt());
    }

    public record ItemResponse(
            UUID id,
            ItemType itemType,
            String code,
            String description,
            String unit,
            BigDecimal coefficient
    ) {
        public static ItemResponse from(CompositionItem i) {
            if (i.getItemType() == ItemType.COMPOSITION) {
                var child = i.getChildComposition();
                return new ItemResponse(i.getId(), i.getItemType(),
                        child.getSinapiCode(), child.getDescription(), child.getUnit(),
                        i.getCoefficient());
            } else {
                var mat = i.getMaterial();
                return new ItemResponse(i.getId(), i.getItemType(),
                        mat.getSinapiCode(), mat.getDescription(), mat.getUnit(),
                        i.getCoefficient());
            }
        }
    }
}
