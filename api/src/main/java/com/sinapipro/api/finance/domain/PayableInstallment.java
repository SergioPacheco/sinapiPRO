package com.sinapipro.api.finance.domain;

import com.sinapipro.api.shared.domain.TenantAwareEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "payable_installment")
public class PayableInstallment extends TenantAwareEntity {
    @Column(name = "payable_id", nullable = false) private UUID payableId;
    @Column(name = "installment_number", nullable = false) private int installmentNumber;
    @Column(name = "due_date", nullable = false) private LocalDate dueDate;
    @Column(nullable = false, precision = 18, scale = 2) private BigDecimal amount;
    @Column(name = "paid_amount", precision = 18, scale = 2) private BigDecimal paidAmount = BigDecimal.ZERO;
    @Column(name = "paid_date") private LocalDate paidDate;
    @Column(precision = 18, scale = 2) private BigDecimal discount = BigDecimal.ZERO;
    @Column(precision = 18, scale = 2) private BigDecimal interest = BigDecimal.ZERO;
    @Column(precision = 18, scale = 2) private BigDecimal fine = BigDecimal.ZERO;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private InstallmentStatus status = InstallmentStatus.OPEN;
    @Column(name = "payment_method", length = 30) private String paymentMethod;
    @Column(name = "bank_account_id") private UUID bankAccountId;
    @Column(name = "check_number", length = 20) private String checkNumber;
    @Column(length = 300) private String notes;

    protected PayableInstallment() {}

    public PayableInstallment(UUID payableId, int installmentNumber, LocalDate dueDate, BigDecimal amount) {
        this.payableId = payableId;
        this.installmentNumber = installmentNumber;
        this.dueDate = dueDate;
        this.amount = amount;
    }

    public UUID getPayableId() { return payableId; }
    public int getInstallmentNumber() { return installmentNumber; }
    public LocalDate getDueDate() { return dueDate; }
    public BigDecimal getAmount() { return amount; }
    public BigDecimal getPaidAmount() { return paidAmount; }
    public LocalDate getPaidDate() { return paidDate; }
    public BigDecimal getDiscount() { return discount; }
    public BigDecimal getInterest() { return interest; }
    public BigDecimal getFine() { return fine; }
    public InstallmentStatus getStatus() { return status; }
    public String getPaymentMethod() { return paymentMethod; }
    public UUID getBankAccountId() { return bankAccountId; }
    public String getCheckNumber() { return checkNumber; }
    public String getNotes() { return notes; }

    public void pay(BigDecimal paidAmount, LocalDate paidDate, String paymentMethod, UUID bankAccountId) {
        this.paidAmount = paidAmount;
        this.paidDate = paidDate;
        this.paymentMethod = paymentMethod;
        this.bankAccountId = bankAccountId;
        this.status = InstallmentStatus.PAID;
    }

    public void applyCharges(BigDecimal interest, BigDecimal fine, BigDecimal discount) {
        this.interest = interest;
        this.fine = fine;
        this.discount = discount;
    }

    public BigDecimal getNetAmount() {
        return amount.add(interest).add(fine).subtract(discount);
    }

    public void markOverdue() { this.status = InstallmentStatus.OVERDUE; }
}
