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

    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", nullable = false, length = 20)
    private ItemType itemType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id")
    private Material material;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_composition_id")
    private Composition childComposition;

    @Column(nullable = false, precision = 14, scale = 6)
    private BigDecimal coefficient;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected CompositionItem() {}

    /** Constructor for MATERIAL, LABOR, EQUIPMENT items */
    public CompositionItem(Composition composition, Material material, BigDecimal coefficient, ItemType itemType) {
        if (itemType == ItemType.COMPOSITION) {
            throw new IllegalArgumentException("Use constructor with childComposition for COMPOSITION type");
        }
        this.composition = composition;
        this.material = material;
        this.coefficient = coefficient;
        this.itemType = itemType;
    }

    /** Constructor for COMPOSITION (auxiliary composition) items — infers type automatically */
    public CompositionItem(Composition composition, Composition childComposition, BigDecimal coefficient) {
        this.composition = composition;
        this.childComposition = childComposition;
        this.coefficient = coefficient;
        this.itemType = ItemType.COMPOSITION;
    }

    /** Legacy constructor for backward compatibility (defaults to MATERIAL) */
    public CompositionItem(Composition composition, Material material, BigDecimal coefficient) {
        this(composition, material, coefficient, ItemType.MATERIAL);
    }

    public UUID getId() { return id; }
    public Composition getComposition() { return composition; }
    public ItemType getItemType() { return itemType; }
    public Material getMaterial() { return material; }
    public Composition getChildComposition() { return childComposition; }
    public BigDecimal getCoefficient() { return coefficient; }
    public Instant getCreatedAt() { return createdAt; }
}
