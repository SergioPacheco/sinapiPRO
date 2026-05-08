package com.sinapipro.api.sinapi.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "material_price", uniqueConstraints = @UniqueConstraint(columnNames = {"material_id", "state", "reference_month"}))
public class MaterialPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id", nullable = false)
    private Material material;

    @Column(nullable = false, length = 2)
    private String state;

    @Column(name = "reference_month", nullable = false)
    private LocalDate referenceMonth;

    @Column(nullable = false, precision = 14, scale = 4)
    private BigDecimal price;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void prePersist() { createdAt = Instant.now(); }

    protected MaterialPrice() {}

    public MaterialPrice(Material material, String state, LocalDate referenceMonth, BigDecimal price) {
        this.material = material;
        this.state = state;
        this.referenceMonth = referenceMonth;
        this.price = price;
    }

    public UUID getId() { return id; }
    public Material getMaterial() { return material; }
    public String getState() { return state; }
    public LocalDate getReferenceMonth() { return referenceMonth; }
    public BigDecimal getPrice() { return price; }
}
