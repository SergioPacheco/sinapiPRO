package com.sinapipro.api.commercial.domain;

import com.sinapipro.api.shared.domain.TenantAwareEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "sale_contract")
public class SaleContract extends TenantAwareEntity {
    @Column(name = "development_id", nullable = false) private UUID developmentId;
    @Column(name = "contract_number", nullable = false, unique = true, length = 30) private String contractNumber;
    @Column(name = "contract_date", nullable = false) private LocalDate contractDate;
    @Column(nullable = false, length = 20) private String status = "PROPOSAL";
    @Column(name = "total_amount", nullable = false, precision = 18, scale = 2) private BigDecimal totalAmount;
    @Column(name = "down_payment", precision = 18, scale = 2) private BigDecimal downPayment;
    @Column(name = "financed_amount", precision = 18, scale = 2) private BigDecimal financedAmount;
    @Column(name = "installment_count", nullable = false) private int installmentCount = 1;
    @Column(name = "index_id") private UUID indexId;
    @Column(name = "interest_rate", precision = 8, scale = 4) private BigDecimal interestRate;
    @Column(name = "amortization_type", length = 10) private String amortizationType = "PRICE";
    @Column(name = "signing_date") private LocalDate signingDate;
    @Column(name = "cancellation_date") private LocalDate cancellationDate;
    @Column(name = "cancellation_reason", length = 500) private String cancellationReason;
    @Column(name = "cancellation_fine_pct", precision = 5, scale = 2) private BigDecimal cancellationFinePct;
    @Column(name = "transfer_date") private LocalDate transferDate;
    @Column(name = "transferred_to_contract_id") private UUID transferredToContractId;
    @Column(name = "broker_id") private UUID brokerId;
    @Column(name = "commission_rate", precision = 5, scale = 2) private BigDecimal commissionRate;
    @Column(name = "commission_amount", precision = 18, scale = 2) private BigDecimal commissionAmount;
    @Column(columnDefinition = "text") private String notes;
    @Column(name = "updated_at", nullable = false) private Instant updatedAt;

    protected SaleContract() {}

    public SaleContract(UUID developmentId, String contractNumber, LocalDate contractDate,
                         BigDecimal totalAmount, int installmentCount, String amortizationType) {
        this.developmentId = developmentId;
        this.contractNumber = contractNumber;
        this.contractDate = contractDate;
        this.totalAmount = totalAmount;
        this.installmentCount = installmentCount;
        this.amortizationType = amortizationType;
    }

    public UUID getDevelopmentId() { return developmentId; }
    public String getContractNumber() { return contractNumber; }
    public LocalDate getContractDate() { return contractDate; }
    public String getStatus() { return status; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public BigDecimal getDownPayment() { return downPayment; }
    public BigDecimal getFinancedAmount() { return financedAmount; }
    public int getInstallmentCount() { return installmentCount; }
    public UUID getIndexId() { return indexId; }
    public BigDecimal getInterestRate() { return interestRate; }
    public String getAmortizationType() { return amortizationType; }
    public LocalDate getSigningDate() { return signingDate; }
    public LocalDate getCancellationDate() { return cancellationDate; }
    public String getCancellationReason() { return cancellationReason; }
    public BigDecimal getCancellationFinePct() { return cancellationFinePct; }
    public UUID getBrokerId() { return brokerId; }
    public BigDecimal getCommissionRate() { return commissionRate; }
    public BigDecimal getCommissionAmount() { return commissionAmount; }
    public String getNotes() { return notes; }

    public void setDownPayment(BigDecimal dp) { this.downPayment = dp; }
    public void setFinancedAmount(BigDecimal fa) { this.financedAmount = fa; }
    public void setIndexId(UUID indexId) { this.indexId = indexId; }
    public void setInterestRate(BigDecimal rate) { this.interestRate = rate; }
    public void setBroker(UUID brokerId, BigDecimal commissionRate) {
        this.brokerId = brokerId;
        this.commissionRate = commissionRate;
        this.commissionAmount = totalAmount.multiply(commissionRate).divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
    }

    public void sign(LocalDate signingDate) {
        this.status = "SIGNED";
        this.signingDate = signingDate;
    }

    public void activate() { this.status = "ACTIVE"; }

    public void cancel(LocalDate date, String reason, BigDecimal finePct) {
        this.status = "CANCELLED";
        this.cancellationDate = date;
        this.cancellationReason = reason;
        this.cancellationFinePct = finePct;
    }

    public void transfer(UUID newContractId, LocalDate date) {
        this.status = "TRANSFERRED";
        this.transferredToContractId = newContractId;
        this.transferDate = date;
    }

    public void complete() { this.status = "COMPLETED"; }
}
