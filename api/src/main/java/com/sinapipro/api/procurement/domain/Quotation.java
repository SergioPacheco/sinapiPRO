package com.sinapipro.api.procurement.domain;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "quotation")
public class Quotation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_request_id", nullable = false)
    private PurchaseRequest purchaseRequest;

    @Column(nullable = false, length = 20)
    private String status;

    private LocalDate deadline;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "quotation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuotationResponse> responses = new ArrayList<>();

    @PrePersist
    void prePersist() { createdAt = Instant.now(); }

    protected Quotation() {}

    public Quotation(PurchaseRequest purchaseRequest, LocalDate deadline) {
        this.purchaseRequest = purchaseRequest;
        this.status = "OPEN";
        this.deadline = deadline;
    }

    public UUID getId() { return id; }
    public PurchaseRequest getPurchaseRequest() { return purchaseRequest; }
    public String getStatus() { return status; }
    public LocalDate getDeadline() { return deadline; }
    public List<QuotationResponse> getResponses() { return responses; }

    public void close() { this.status = "CLOSED"; }
}
