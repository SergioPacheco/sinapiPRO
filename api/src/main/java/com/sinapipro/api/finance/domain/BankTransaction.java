package com.sinapipro.api.finance.domain;

import com.sinapipro.api.shared.domain.TenantAwareEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "bank_transaction")
public class BankTransaction extends TenantAwareEntity {
    @Column(name = "bank_account_id", nullable = false) private UUID bankAccountId;
    @Column(name = "transaction_date", nullable = false) private LocalDate transactionDate;
    @Column(nullable = false, length = 20) private String type; // DEBIT, CREDIT, TRANSFER
    @Column(nullable = false, precision = 18, scale = 2) private BigDecimal amount;
    @Column(name = "balance_after", precision = 18, scale = 2) private BigDecimal balanceAfter;
    @Column(nullable = false, length = 300) private String description;
    @Column(name = "reference_type", length = 30) private String referenceType;
    @Column(name = "reference_id") private UUID referenceId;
    @Column(nullable = false) private boolean reconciled;
    @Column(name = "reconciled_at") private Instant reconciledAt;

    protected BankTransaction() {}

    public BankTransaction(UUID bankAccountId, LocalDate transactionDate, String type,
                           BigDecimal amount, String description) {
        this.bankAccountId = bankAccountId;
        this.transactionDate = transactionDate;
        this.type = type;
        this.amount = amount;
        this.description = description;
    }

    public UUID getBankAccountId() { return bankAccountId; }
    public LocalDate getTransactionDate() { return transactionDate; }
    public String getType() { return type; }
    public BigDecimal getAmount() { return amount; }
    public BigDecimal getBalanceAfter() { return balanceAfter; }
    public String getDescription() { return description; }
    public String getReferenceType() { return referenceType; }
    public UUID getReferenceId() { return referenceId; }
    public boolean isReconciled() { return reconciled; }
    public Instant getReconciledAt() { return reconciledAt; }

    public void setBalanceAfter(BigDecimal balanceAfter) { this.balanceAfter = balanceAfter; }
    public void setReference(String type, UUID id) { this.referenceType = type; this.referenceId = id; }

    public void reconcile() {
        this.reconciled = true;
        this.reconciledAt = Instant.now();
    }
}
