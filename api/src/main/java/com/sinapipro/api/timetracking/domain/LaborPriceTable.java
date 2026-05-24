package com.sinapipro.api.timetracking.domain;
import com.sinapipro.api.shared.domain.TenantAwareEntity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "labor_price_table")
public class LaborPriceTable extends TenantAwareEntity {
    @Column(nullable = false, length = 100) private String name;
    @Column(name = "valid_from", nullable = false) private LocalDate validFrom;
    @Column(nullable = false) private boolean active = true;

    protected LaborPriceTable() {}

    public LaborPriceTable(String name, LocalDate validFrom) {
        this.name = name; this.validFrom = validFrom;
    }

    public String getName() { return name; }
    public LocalDate getValidFrom() { return validFrom; }
    public boolean isActive() { return active; }
    public void deactivate() { this.active = false; }
}
