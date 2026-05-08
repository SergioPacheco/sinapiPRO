package com.sinapipro.api.sinapi.api;

import com.sinapipro.api.sinapi.domain.Composition;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record CompositionResponse(
        UUID id,
        String sinapiCode,
        String description,
        String unit,
        String groupName,
        List<ItemResponse> items,
        Instant createdAt
) {
    public static CompositionResponse from(Composition c) {
        return new CompositionResponse(c.getId(), c.getSinapiCode(), c.getDescription(),
                c.getUnit(), c.getGroupName(),
                c.getItems().stream().map(i -> new ItemResponse(
                        i.getMaterial().getSinapiCode(),
                        i.getMaterial().getDescription(),
                        i.getMaterial().getUnit(),
                        i.getCoefficient()
                )).toList(),
                c.getCreatedAt());
    }

    public record ItemResponse(String materialCode, String materialDescription, String materialUnit, java.math.BigDecimal coefficient) {}
}
