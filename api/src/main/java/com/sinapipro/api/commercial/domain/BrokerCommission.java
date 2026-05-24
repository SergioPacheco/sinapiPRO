package com.sinapipro.api.commercial.domain;
import com.sinapipro.api.shared.domain.TenantAwareEntity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "broker_commission")
public class BrokerCommission extends TenantAwareEntity {
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "proposal_id", nullable = false) private SalesProposal proposal;
    @Column(name = "broker_name", nullable = false, length = 200) private String brokerName;
    @Column(nullable = false, precision = 5, scale = 4) private BigDecimal percentage;
    @Column(nullable = false, precision = 18, scale = 2) private BigDecimal amount;
    @Column(nullable = false, length = 20) private String status;
    @Column(name = "paid_date") private LocalDate paidDate;

    protected BrokerCommission() {}
    public BrokerCommission(SalesProposal proposal, String brokerName, BigDecimal percentage, BigDecimal amount) {
        this.proposal = proposal; this.brokerName = brokerName; this.percentage = percentage;
        this.amount = amount; this.status = "PENDING";
    }

    public SalesProposal getProposal() { return proposal; }
    public String getBrokerName() { return brokerName; }
    public BigDecimal getPercentage() { return percentage; }
    public BigDecimal getAmount() { return amount; }
    public String getStatus() { return status; }
    public LocalDate getPaidDate() { return paidDate; }
    public void pay(LocalDate paidDate) { this.paidDate = paidDate; this.status = "PAID"; }
}
