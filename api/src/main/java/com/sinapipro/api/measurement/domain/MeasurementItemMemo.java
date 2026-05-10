package com.sinapipro.api.measurement.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "measurement_item_memo")
public class MeasurementItemMemo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "measurement_item_id", nullable = false, unique = true)
    private UUID measurementItemId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private List<MemoLine> lines;

    @Column(precision = 14, scale = 4)
    private BigDecimal result;

    public MeasurementItemMemo() {}

    public UUID getId() { return id; }
    public UUID getMeasurementItemId() { return measurementItemId; }
    public List<MemoLine> getLines() { return lines; }
    public BigDecimal getResult() { return result; }

    public void setMeasurementItemId(UUID id) { this.measurementItemId = id; }
    public void setLines(List<MemoLine> lines) { this.lines = lines; }
    public void setResult(BigDecimal result) { this.result = result; }

    public record MemoLine(String description, String formula, BigDecimal value) {}
}
