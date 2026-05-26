package com.sinapipro.api.finance.domain;

import com.sinapipro.api.shared.domain.TenantAwareEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Cheque recebido de cliente — controle de custódia e compensação.
 */
@Entity
@Table(name = "check_received")
public class CheckReceived extends TenantAwareEntity {
    @Column(name = "bank_code", nullable = false, length = 10) private String bankCode;
    @Column(name = "agency", nullable = false, length = 20) private String agency;
    @Column(name = "account_number", nullable = false, length = 30) private String accountNumber;
    @Column(name = "check_number", nullable = false, length = 20) private String checkNumber;
    @Column(nullable = false, precision = 18, scale = 2) private BigDecimal amount;
    @Column(name = "issue_date", nullable = false) private LocalDate issueDate;
    @Column(name = "due_date") private LocalDate dueDate;
    @Column(name = "issuer_name", nullable = false, length = 200) private String issuerName;
    @Column(name = "issuer_document", length = 20) private String issuerDocument;
    @Column(name = "receivable_installment_id") private UUID receivableInstallmentId;
    @Column(name = "custody_bank_account_id") private UUID custodyBankAccountId;
    @Column(name = "custody_date") private LocalDate custodyDate;
    @Column(name = "cleared_date") private LocalDate clearedDate;
    @Column(name = "returned_date") private LocalDate returnedDate;
    @Column(name = "return_reason", length = 200) private String returnReason;
    @Column(nullable = false, length = 20) private String status = "RECEIVED";
    @Column(length = 300) private String notes;

    protected CheckReceived() {}

    public CheckReceived(String bankCode, String agency, String accountNumber, String checkNumber,
                         BigDecimal amount, LocalDate issueDate, LocalDate dueDate,
                         String issuerName, String issuerDocument) {
        this.bankCode = bankCode; this.agency = agency; this.accountNumber = accountNumber;
        this.checkNumber = checkNumber; this.amount = amount; this.issueDate = issueDate;
        this.dueDate = dueDate; this.issuerName = issuerName; this.issuerDocument = issuerDocument;
    }

    public String getBankCode() { return bankCode; }
    public String getAgency() { return agency; }
    public String getAccountNumber() { return accountNumber; }
    public String getCheckNumber() { return checkNumber; }
    public BigDecimal getAmount() { return amount; }
    public LocalDate getIssueDate() { return issueDate; }
    public LocalDate getDueDate() { return dueDate; }
    public String getIssuerName() { return issuerName; }
    public String getIssuerDocument() { return issuerDocument; }
    public UUID getReceivableInstallmentId() { return receivableInstallmentId; }
    public UUID getCustodyBankAccountId() { return custodyBankAccountId; }
    public LocalDate getCustodyDate() { return custodyDate; }
    public LocalDate getClearedDate() { return clearedDate; }
    public LocalDate getReturnedDate() { return returnedDate; }
    public String getReturnReason() { return returnReason; }
    public String getStatus() { return status; }
    public String getNotes() { return notes; }

    public void setReceivableInstallmentId(UUID id) { this.receivableInstallmentId = id; }
    public void setNotes(String notes) { this.notes = notes; }

    /** Enviar para custódia bancária */
    public void sendToCustody(UUID bankAccountId) {
        this.custodyBankAccountId = bankAccountId;
        this.custodyDate = LocalDate.now();
        this.status = "IN_CUSTODY";
    }

    /** Compensar cheque */
    public void clear(LocalDate clearedDate) {
        this.clearedDate = clearedDate;
        this.status = "CLEARED";
    }

    /** Devolver cheque (sem fundos, etc.) */
    public void returnCheck(String reason) {
        this.returnedDate = LocalDate.now();
        this.returnReason = reason;
        this.status = "RETURNED";
    }

    public void cancel() { this.status = "CANCELLED"; }
}
