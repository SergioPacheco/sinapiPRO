package com.sinapipro.api.procurement.domain;
import com.sinapipro.api.shared.domain.TenantAwareEntity;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "quotation")
public class Quotation extends TenantAwareEntity {


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_request_id", nullable = false)
    private PurchaseRequest purchaseRequest;

    @Column(nullable = false, length = 20)
    private String status;

    private LocalDate deadline;


    @OneToMany(mappedBy = "quotation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuotationResponse> responses = new ArrayList<>();


    protected Quotation() {}

    public Quotation(PurchaseRequest purchaseRequest, LocalDate deadline) {
        this.purchaseRequest = purchaseRequest;
        this.status = "OPEN";
        this.deadline = deadline;
    }

    public PurchaseRequest getPurchaseRequest() { return purchaseRequest; }
    public String getStatus() { return status; }
    public LocalDate getDeadline() { return deadline; }
    public List<QuotationResponse> getResponses() { return responses; }

    public void close() { this.status = "CLOSED"; }
}
