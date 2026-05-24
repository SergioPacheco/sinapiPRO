package com.sinapipro.api.finance.domain;
import com.sinapipro.api.shared.domain.TenantAwareEntity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "check_issuance")
public class CheckIssuance extends TenantAwareEntity {
    @Column(name = "bank_account_id", nullable = false) private UUID bankAccountId;
    @Column(name = "check_number", nullable = false, length = 20) private String checkNumber;
    @Column(nullable = false, precision = 18, scale = 2) private BigDecimal amount;
    @Column(name = "issue_date", nullable = false) private LocalDate issueDate;
    @Column(name = "due_date") private LocalDate dueDate;
    @Column(name = "payee_name", nullable = false, length = 200) private String payeeName;
    @Column(name = "payee_document", length = 20) private String payeeDocument;
    @Column(nullable = false, length = 20) private String status = "ISSUED";
    @Column(name = "cleared_date") private LocalDate clearedDate;
    @Column(name = "payable_installment_id") private UUID payableInstallmentId;
    @Column(length = 300) private String notes;

    protected CheckIssuance() {}

    public CheckIssuance(UUID bankAccountId, String checkNumber, BigDecimal amount,
                         LocalDate issueDate, LocalDate dueDate, String payeeName, String payeeDocument) {
        this.bankAccountId = bankAccountId;
        this.checkNumber = checkNumber;
        this.amount = amount;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.payeeName = payeeName;
        this.payeeDocument = payeeDocument;
    }

    public UUID getBankAccountId() { return bankAccountId; }
    public String getCheckNumber() { return checkNumber; }
    public BigDecimal getAmount() { return amount; }
    public LocalDate getIssueDate() { return issueDate; }
    public LocalDate getDueDate() { return dueDate; }
    public String getPayeeName() { return payeeName; }
    public String getPayeeDocument() { return payeeDocument; }
    public String getStatus() { return status; }
    public LocalDate getClearedDate() { return clearedDate; }
    public UUID getPayableInstallmentId() { return payableInstallmentId; }

    public void setPayableInstallmentId(UUID id) { this.payableInstallmentId = id; }

    public void clear(LocalDate clearedDate) {
        this.status = "CLEARED";
        this.clearedDate = clearedDate;
    }

    public void cancel() { this.status = "CANCELLED"; }
}
