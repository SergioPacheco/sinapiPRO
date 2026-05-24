package com.sinapipro.api.timetracking.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "hour_type")
public class HourType {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(nullable = false, unique = true, length = 10) private String code;
    @Column(nullable = false, length = 60) private String name;
    @Column(nullable = false, precision = 4, scale = 2) private BigDecimal multiplier = BigDecimal.ONE;
    @Column(nullable = false) private boolean active = true;

    protected HourType() {}
    public HourType(String code, String name, BigDecimal multiplier) {
        this.code = code; this.name = name; this.multiplier = multiplier;
    }

    public UUID getId() { return id; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public BigDecimal getMultiplier() { return multiplier; }
    public boolean isActive() { return active; }
}
