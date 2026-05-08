package com.sinapipro.api.sinapi.api;

import com.sinapipro.api.sinapi.domain.Material;

import java.time.Instant;
import java.util.UUID;

public record MaterialResponse(UUID id, String sinapiCode, String description, String unit, String origin, Instant createdAt) {
    public static MaterialResponse from(Material m) {
        return new MaterialResponse(m.getId(), m.getSinapiCode(), m.getDescription(), m.getUnit(), m.getOrigin(), m.getCreatedAt());
    }
}
