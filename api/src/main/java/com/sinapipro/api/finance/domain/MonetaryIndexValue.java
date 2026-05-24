package com.sinapipro.api.finance.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "monetary_index_value", uniqueConstraints = @UniqueConstraint(columnNames = {"index_id", "reference_month"}))
public class MonetaryIndexValue {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(name = "index_id", nullable = false) private UUID indexId;
    @Column(name = "reference_month", nullable = false) private LocalDate referenceMonth;
    @Column(nullable = false, precision = 12, scale = 6) private BigDecimal value;
    @Column(precision = 14, scale = 6) private BigDecimal accumulated;

    protected MonetaryIndexValue() {}
    public MonetaryIndexValue(UUID indexId, LocalDate referenceMonth, BigDecimal value, BigDecimal accumulated) {
        this.indexId = indexId; this.referenceMonth = referenceMonth; this.value = value; this.accumulated = accumulated;
    }

    public UUID getId() { return id; }
    public UUID getIndexId() { return indexId; }
    public LocalDate getReferenceMonth() { return referenceMonth; }
    public BigDecimal getValue() { return value; }
    public BigDecimal getAccumulated() { return accumulated; }
}
