package com.sinapipro.api.commercial.domain;
import com.sinapipro.api.shared.domain.TenantAwareEntity;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "price_table")
public class PriceTable extends TenantAwareEntity {
    @Column(name = "development_id", nullable = false) private UUID developmentId;
    @Column(nullable = false, length = 100) private String name;
    @Column(name = "valid_from", nullable = false) private LocalDate validFrom;
    @Column(name = "valid_until") private LocalDate validUntil;
    @Column(name = "index_id") private UUID indexId;
    @Column(nullable = false) private boolean active = true;

    protected PriceTable() {}

    public PriceTable(UUID developmentId, String name, LocalDate validFrom, UUID indexId) {
        this.developmentId = developmentId; this.name = name; this.validFrom = validFrom; this.indexId = indexId;
    }

    public UUID getDevelopmentId() { return developmentId; }
    public String getName() { return name; }
    public LocalDate getValidFrom() { return validFrom; }
    public LocalDate getValidUntil() { return validUntil; }
    public UUID getIndexId() { return indexId; }
    public boolean isActive() { return active; }
    public void deactivate() { this.active = false; }
}
