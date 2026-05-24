package com.sinapipro.api.finance.domain;

import com.sinapipro.api.shared.domain.TenantAwareEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "receivable_installment")
public class ReceivableInstallment extends TenantAwareEntity {
    @Column(name = "receivable_id", nullable = false) private UUID receivableId;
    @Column(name = "installment_number", nullable = false) private int installmentNumber;
    @Column(name = "due_date", nullable = false) private LocalDate dueDate;
    @Column(nullable = false, precision = 18, scale = 2) private BigDecimal amount;
    @Column(name = "received_amount", precision = 18, scale = 2) private BigDecimal receivedAmount = BigDecimal.ZERO;
    @Column(name = "received_date") private LocalDate receivedDate;
    @Column(precision = 18, scale = 2) private BigDecimal discount = BigDecimal.ZERO;
    @Column(precision = 18, scale = 2) private BigDecimal interest = BigDecimal.ZERO;
    @Column(precision = 18, scale = 2) private BigDecimal fine = BigDecimal.ZERO;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private InstallmentStatus status = InstallmentStatus.OPEN;
    @Column(name = "boleto_number", length = 50) private String boletoNumber;
    @Column(name = "boleto_barcode", length = 60) private String boletoBarcode;
    @Column(name = "our_number", length = 30) private String ourNumber;
    @Column(name = "remittance_file", length = 100) private String remittanceFile;
    @Column(name = "return_file", length = 100) private String returnFile;
    @Column(name = "bank_account_id") private UUID bankAccountId;

    protected ReceivableInstallment() {}

    public ReceivableInstallment(UUID receivableId, int installmentNumber, LocalDate dueDate, BigDecimal amount) {
        this.receivableId = receivableId;
        this.installmentNumber = installmentNumber;
        this.dueDate = dueDate;
        this.amount = amount;
    }

    public UUID getReceivableId() { return receivableId; }
    public int getInstallmentNumber() { return installmentNumber; }
    public LocalDate getDueDate() { return dueDate; }
    public BigDecimal getAmount() { return amount; }
    public BigDecimal getReceivedAmount() { return receivedAmount; }
    public LocalDate getReceivedDate() { return receivedDate; }
    public BigDecimal getDiscount() { return discount; }
    public BigDecimal getInterest() { return interest; }
    public BigDecimal getFine() { return fine; }
    public InstallmentStatus getStatus() { return status; }
    public String getBoletoNumber() { return boletoNumber; }
    public String getBoletoBarcode() { return boletoBarcode; }
    public String getOurNumber() { return ourNumber; }
    public String getRemittanceFile() { return remittanceFile; }
    public String getReturnFile() { return returnFile; }
    public UUID getBankAccountId() { return bankAccountId; }

    public void setBoleto(String boletoNumber, String boletoBarcode, String ourNumber) {
        this.boletoNumber = boletoNumber;
        this.boletoBarcode = boletoBarcode;
        this.ourNumber = ourNumber;
    }

    public void setRemittanceFile(String file) { this.remittanceFile = file; }
    public void setReturnFile(String file) { this.returnFile = file; }
    public void setBankAccountId(UUID bankAccountId) { this.bankAccountId = bankAccountId; }

    public void receive(BigDecimal receivedAmount, LocalDate receivedDate,
                        BigDecimal interest, BigDecimal fine, BigDecimal discount) {
        this.receivedAmount = receivedAmount;
        this.receivedDate = receivedDate;
        this.interest = interest != null ? interest : BigDecimal.ZERO;
        this.fine = fine != null ? fine : BigDecimal.ZERO;
        this.discount = discount != null ? discount : BigDecimal.ZERO;
        this.status = InstallmentStatus.PAID;
    }

    public void markOverdue() { this.status = InstallmentStatus.OVERDUE; }

    public BigDecimal getNetAmount() {
        return amount.add(interest).add(fine).subtract(discount);
    }
}
