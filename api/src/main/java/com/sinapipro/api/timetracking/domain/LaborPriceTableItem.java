package com.sinapipro.api.timetracking.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "labor_price_table_item")
public class LaborPriceTableItem {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(name = "table_id", nullable = false) private UUID tableId;
    @Column(nullable = false, length = 80) private String role;
    @Column(name = "hourly_rate", nullable = false, precision = 14, scale = 4) private BigDecimal hourlyRate;
    @Column(name = "monthly_rate", precision = 14, scale = 2) private BigDecimal monthlyRate;
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt;

    @PrePersist void prePersist() { createdAt = Instant.now(); }
    protected LaborPriceTableItem() {}

    public LaborPriceTableItem(UUID tableId, String role, BigDecimal hourlyRate, BigDecimal monthlyRate) {
        this.tableId = tableId; this.role = role; this.hourlyRate = hourlyRate; this.monthlyRate = monthlyRate;
    }

    public UUID getId() { return id; }
    public UUID getTableId() { return tableId; }
    public String getRole() { return role; }
    public BigDecimal getHourlyRate() { return hourlyRate; }
    public BigDecimal getMonthlyRate() { return monthlyRate; }
}
