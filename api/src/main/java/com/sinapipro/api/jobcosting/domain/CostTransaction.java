package com.sinapipro.api.jobcosting.domain;
import com.sinapipro.api.shared.domain.TenantAwareEntity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "cost_transaction")
public class CostTransaction extends TenantAwareEntity {


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cost_code_id", nullable = false)
    private CostCode costCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CostTransactionType type;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(length = 300)
    private String description;

    @Column(name = "reference_id")
    private UUID referenceId;

    @Column(name = "transaction_date", nullable = false)
    private LocalDate transactionDate;



    protected CostTransaction() {}

    public CostTransaction(CostCode costCode, CostTransactionType type, BigDecimal amount,
                           String description, UUID referenceId, LocalDate transactionDate) {
        this.costCode = costCode;
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.referenceId = referenceId;
        this.transactionDate = transactionDate;
    }

    public CostCode getCostCode() { return costCode; }
    public CostTransactionType getType() { return type; }
    public BigDecimal getAmount() { return amount; }
    public String getDescription() { return description; }
    public UUID getReferenceId() { return referenceId; }
    public LocalDate getTransactionDate() { return transactionDate; }
}
