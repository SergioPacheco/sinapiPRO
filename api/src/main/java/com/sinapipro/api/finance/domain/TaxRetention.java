package com.sinapipro.api.finance.domain;
import com.sinapipro.api.shared.domain.TenantAwareEntity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "tax_retention")
public class TaxRetention extends TenantAwareEntity {
    @Column(name = "payable_id", nullable = false) private UUID payableId;
    @Enumerated(EnumType.STRING) @Column(name = "tax_type", nullable = false, length = 20) private TaxType taxType;
    @Column(name = "base_amount", nullable = false, precision = 18, scale = 2) private BigDecimal baseAmount;
    @Column(nullable = false, precision = 8, scale = 4) private BigDecimal rate;
    @Column(nullable = false, precision = 18, scale = 2) private BigDecimal amount;
    @Column(name = "due_date") private LocalDate dueDate;
    @Column(nullable = false) private boolean paid;
    @Column(name = "guide_number", length = 30) private String guideNumber;

    protected TaxRetention() {}

    public TaxRetention(UUID payableId, TaxType taxType, BigDecimal baseAmount, BigDecimal rate) {
        this.payableId = payableId;
        this.taxType = taxType;
        this.baseAmount = baseAmount;
        this.rate = rate;
        this.amount = baseAmount.multiply(rate).divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
    }

    public UUID getPayableId() { return payableId; }
    public TaxType getTaxType() { return taxType; }
    public BigDecimal getBaseAmount() { return baseAmount; }
    public BigDecimal getRate() { return rate; }
    public BigDecimal getAmount() { return amount; }
    public LocalDate getDueDate() { return dueDate; }
    public boolean isPaid() { return paid; }
    public String getGuideNumber() { return guideNumber; }

    public void markPaid(String guideNumber) {
        this.paid = true;
        this.guideNumber = guideNumber;
    }
}
