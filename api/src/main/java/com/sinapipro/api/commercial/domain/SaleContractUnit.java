package com.sinapipro.api.commercial.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "sale_contract_unit")
public class SaleContractUnit {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(name = "contract_id", nullable = false) private UUID contractId;
    @Column(name = "unit_id", nullable = false) private UUID unitId;
    @Column(nullable = false, precision = 18, scale = 2) private BigDecimal price;

    protected SaleContractUnit() {}
    public SaleContractUnit(UUID contractId, UUID unitId, BigDecimal price) {
        this.contractId = contractId; this.unitId = unitId; this.price = price;
    }

    public UUID getId() { return id; }
    public UUID getContractId() { return contractId; }
    public UUID getUnitId() { return unitId; }
    public BigDecimal getPrice() { return price; }
}
