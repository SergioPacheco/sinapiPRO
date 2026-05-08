package com.sinapipro.api.contract.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "change_order", uniqueConstraints = @UniqueConstraint(columnNames = {"contract_id", "number"}))
public class ChangeOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", nullable = false)
    private Contract contract;

    @Column(nullable = false)
    private Integer number;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ChangeOrderStatus status;

    @Column(columnDefinition = "text")
    private String justification;

    @Column(name = "approved_at")
    private Instant approvedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void prePersist() { createdAt = Instant.now(); }

    protected ChangeOrder() {}

    public ChangeOrder(Contract contract, Integer number, String description, BigDecimal amount, String justification) {
        this.contract = contract;
        this.number = number;
        this.description = description;
        this.amount = amount;
        this.justification = justification;
        this.status = ChangeOrderStatus.PENDING;
    }

    public UUID getId() { return id; }
    public Contract getContract() { return contract; }
    public Integer getNumber() { return number; }
    public String getDescription() { return description; }
    public BigDecimal getAmount() { return amount; }
    public ChangeOrderStatus getStatus() { return status; }
    public String getJustification() { return justification; }
    public Instant getApprovedAt() { return approvedAt; }

    public void approve() {
        if (status != ChangeOrderStatus.PENDING) throw new IllegalStateException("Can only approve PENDING change orders");
        this.status = ChangeOrderStatus.APPROVED;
        this.approvedAt = Instant.now();
    }

    public void reject() {
        if (status != ChangeOrderStatus.PENDING) throw new IllegalStateException("Can only reject PENDING change orders");
        this.status = ChangeOrderStatus.REJECTED;
    }
}
