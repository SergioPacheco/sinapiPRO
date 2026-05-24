package com.sinapipro.api.finance.domain;
import com.sinapipro.api.shared.domain.TenantAwareEntity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "supplier_advance")
public class SupplierAdvance extends TenantAwareEntity {
    @Column(name = "supplier_id", nullable = false) private UUID supplierId;
    @Column(name = "project_id", nullable = false) private UUID projectId;
    @Column(nullable = false, precision = 18, scale = 2) private BigDecimal amount;
    @Column(name = "advance_date", nullable = false) private LocalDate advanceDate;
    @Column(nullable = false, precision = 18, scale = 2) private BigDecimal balance;
    @Column(nullable = false, length = 20) private String status = "ACTIVE";
    @Column(length = 500) private String notes;

    protected SupplierAdvance() {}

    public SupplierAdvance(UUID supplierId, UUID projectId, BigDecimal amount, LocalDate advanceDate) {
        this.supplierId = supplierId;
        this.projectId = projectId;
        this.amount = amount;
        this.advanceDate = advanceDate;
        this.balance = amount;
    }

    public UUID getSupplierId() { return supplierId; }
    public UUID getProjectId() { return projectId; }
    public BigDecimal getAmount() { return amount; }
    public LocalDate getAdvanceDate() { return advanceDate; }
    public BigDecimal getBalance() { return balance; }
    public String getStatus() { return status; }
    public String getNotes() { return notes; }

    public BigDecimal deduct(BigDecimal value) {
        var deducted = value.min(balance);
        this.balance = this.balance.subtract(deducted);
        if (this.balance.signum() == 0) this.status = "SETTLED";
        return deducted;
    }
}
