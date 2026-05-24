package com.sinapipro.api.commercial.domain;
import com.sinapipro.api.shared.domain.TenantAwareEntity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "sale_installment")
public class SaleInstallment extends TenantAwareEntity {
    @Column(name = "contract_id", nullable = false) private UUID contractId;
    @Column(name = "installment_number", nullable = false) private int installmentNumber;
    @Column(nullable = false, length = 30) private String type = "MONTHLY";
    @Column(name = "original_due_date", nullable = false) private LocalDate originalDueDate;
    @Column(name = "current_due_date", nullable = false) private LocalDate currentDueDate;
    @Column(name = "original_amount", nullable = false, precision = 18, scale = 2) private BigDecimal originalAmount;
    @Column(name = "adjusted_amount", nullable = false, precision = 18, scale = 2) private BigDecimal adjustedAmount;
    @Column(name = "paid_amount", precision = 18, scale = 2) private BigDecimal paidAmount = BigDecimal.ZERO;
    @Column(name = "paid_date") private LocalDate paidDate;
    @Column(precision = 18, scale = 2) private BigDecimal interest = BigDecimal.ZERO;
    @Column(precision = 18, scale = 2) private BigDecimal fine = BigDecimal.ZERO;
    @Column(precision = 18, scale = 2) private BigDecimal discount = BigDecimal.ZERO;
    @Column(name = "adjustment_index_value", precision = 12, scale = 6) private BigDecimal adjustmentIndexValue;
    @Column(nullable = false, length = 20) private String status = "FUTURE";
    @Column(name = "boleto_number", length = 50) private String boletoNumber;
    @Column(length = 300) private String notes;

    protected SaleInstallment() {}

    public SaleInstallment(UUID contractId, int installmentNumber, String type,
                            LocalDate dueDate, BigDecimal amount) {
        this.contractId = contractId;
        this.installmentNumber = installmentNumber;
        this.type = type;
        this.originalDueDate = dueDate;
        this.currentDueDate = dueDate;
        this.originalAmount = amount;
        this.adjustedAmount = amount;
    }

    public UUID getContractId() { return contractId; }
    public int getInstallmentNumber() { return installmentNumber; }
    public String getType() { return type; }
    public LocalDate getOriginalDueDate() { return originalDueDate; }
    public LocalDate getCurrentDueDate() { return currentDueDate; }
    public BigDecimal getOriginalAmount() { return originalAmount; }
    public BigDecimal getAdjustedAmount() { return adjustedAmount; }
    public BigDecimal getPaidAmount() { return paidAmount; }
    public LocalDate getPaidDate() { return paidDate; }
    public BigDecimal getInterest() { return interest; }
    public BigDecimal getFine() { return fine; }
    public BigDecimal getDiscount() { return discount; }
    public BigDecimal getAdjustmentIndexValue() { return adjustmentIndexValue; }
    public String getStatus() { return status; }
    public String getBoletoNumber() { return boletoNumber; }

    public void adjust(BigDecimal indexValue) {
        this.adjustmentIndexValue = indexValue;
        this.adjustedAmount = originalAmount.multiply(indexValue).setScale(2, java.math.RoundingMode.HALF_UP);
    }

    public void pay(BigDecimal amount, LocalDate date, BigDecimal interest, BigDecimal fine, BigDecimal discount) {
        this.paidAmount = amount;
        this.paidDate = date;
        this.interest = interest != null ? interest : BigDecimal.ZERO;
        this.fine = fine != null ? fine : BigDecimal.ZERO;
        this.discount = discount != null ? discount : BigDecimal.ZERO;
        this.status = "PAID";
    }

    public void markOverdue() { this.status = "OVERDUE"; }
    public void cancel() { this.status = "CANCELLED"; }
}
