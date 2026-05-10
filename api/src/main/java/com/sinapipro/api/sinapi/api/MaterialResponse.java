package com.sinapipro.api.sinapi.api;

import com.sinapipro.api.sinapi.domain.Material;
import com.sinapipro.api.sinapi.domain.MaterialPrice;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record MaterialResponse(UUID id, String sinapiCode, String description, String unit, String origin,
                                BigDecimal price, String priceState, LocalDate priceMonth, Instant createdAt) {

    public static MaterialResponse from(Material m) {
        return new MaterialResponse(m.getId(), m.getSinapiCode(), m.getDescription(), m.getUnit(), m.getOrigin(),
                null, null, null, m.getCreatedAt());
    }

    public static MaterialResponse from(Material m, String state, LocalDate month, boolean desonerated) {
        BigDecimal price = m.getPrices().stream()
                .filter(p -> p.getState().equals(state) && p.getReferenceMonth().equals(month) && p.isDesonerated() == desonerated)
                .map(MaterialPrice::getPrice)
                .findFirst().orElse(null);
        return new MaterialResponse(m.getId(), m.getSinapiCode(), m.getDescription(), m.getUnit(), m.getOrigin(),
                price, state, month, m.getCreatedAt());
    }
}
