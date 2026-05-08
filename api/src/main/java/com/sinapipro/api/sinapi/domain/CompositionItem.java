package com.sinapipro.api.sinapi.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "composition_item")
public class CompositionItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "composition_id", nullable = false)
    private Composition composition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id", nullable = false)
    private Material material;

    @Column(nullable = false, precision = 14, scale = 6)
    private BigDecimal coefficient;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void prePersist() { createdAt = Instant.now(); }

    protected CompositionItem() {}

    public CompositionItem(Composition composition, Material material, BigDecimal coefficient) {
        this.composition = composition;
        this.material = material;
        this.coefficient = coefficient;
    }

    public UUID getId() { return id; }
    public Composition getComposition() { return composition; }
    public Material getMaterial() { return material; }
    public BigDecimal getCoefficient() { return coefficient; }
}
