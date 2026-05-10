package com.sinapipro.api.registry.domain;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "unit_of_measure")
public class UnitOfMeasure {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(nullable = false, unique = true, length = 10) private String symbol;
    @Column(nullable = false, length = 100) private String description;
    protected UnitOfMeasure() {}
    public UnitOfMeasure(String symbol, String description) { this.symbol = symbol; this.description = description; }
    public UUID getId() { return id; }
    public String getSymbol() { return symbol; }
    public String getDescription() { return description; }
}
