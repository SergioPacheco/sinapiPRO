package com.sinapipro.api.commercial.domain;

import com.sinapipro.api.shared.domain.AuditableEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "sales_proposal")
public class SalesProposal extends AuditableEntity {
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "unit_id", nullable = false) private DevelopmentUnit unit;
    @Column(name = "client_id") private UUID clientId;
    @Column(name = "client_name", nullable = false, length = 200) private String clientName;
    @Column(name = "proposal_date", nullable = false) private LocalDate proposalDate;
    @Column(name = "proposed_price", nullable = false, precision = 18, scale = 2) private BigDecimal proposedPrice;
    @Column(name = "down_payment", precision = 18, scale = 2) private BigDecimal downPayment;
    @Column private int installments = 1;
    @Column(nullable = false, length = 20) private String status;
    @Column(length = 500) private String notes;

    protected SalesProposal() {}
    public SalesProposal(DevelopmentUnit unit, UUID clientId, String clientName, LocalDate proposalDate,
                         BigDecimal proposedPrice, BigDecimal downPayment, int installments, String notes) {
        this.unit = unit; this.clientId = clientId; this.clientName = clientName; this.proposalDate = proposalDate;
        this.proposedPrice = proposedPrice; this.downPayment = downPayment; this.installments = installments;
        this.status = "PENDING"; this.notes = notes;
    }

    public DevelopmentUnit getUnit() { return unit; }
    public UUID getClientId() { return clientId; }
    public String getClientName() { return clientName; }
    public LocalDate getProposalDate() { return proposalDate; }
    public BigDecimal getProposedPrice() { return proposedPrice; }
    public BigDecimal getDownPayment() { return downPayment; }
    public int getInstallments() { return installments; }
    public String getStatus() { return status; }
    public String getNotes() { return notes; }

    public void approve() { this.status = "APPROVED"; unit.reserve(); }
    public void reject() { this.status = "REJECTED"; }
    public void sign() { this.status = "SIGNED"; unit.sell(); }
    public void cancel() { this.status = "CANCELLED"; unit.release(); }
}
