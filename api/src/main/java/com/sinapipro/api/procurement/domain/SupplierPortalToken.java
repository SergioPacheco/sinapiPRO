package com.sinapipro.api.procurement.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Entity
@Table(name = "supplier_portal_token")
public class SupplierPortalToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 64)
    private String token;

    @Column(name = "quotation_id", nullable = false)
    private UUID quotationId;

    @Column(name = "supplier_id", nullable = false)
    private UUID supplierId;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "used_at")
    private Instant usedAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    protected SupplierPortalToken() {}

    public SupplierPortalToken(UUID quotationId, UUID supplierId, int expirationDays) {
        this.token = UUID.randomUUID().toString().replace("-", "") + UUID.randomUUID().toString().replace("-", "");
        this.quotationId = quotationId;
        this.supplierId = supplierId;
        this.expiresAt = Instant.now().plus(expirationDays, ChronoUnit.DAYS);
    }

    public UUID getId() { return id; }
    public String getToken() { return token; }
    public UUID getQuotationId() { return quotationId; }
    public UUID getSupplierId() { return supplierId; }
    public Instant getExpiresAt() { return expiresAt; }
    public Instant getUsedAt() { return usedAt; }
    public boolean isExpired() { return Instant.now().isAfter(expiresAt); }
    public boolean isUsed() { return usedAt != null; }

    public void markUsed() { this.usedAt = Instant.now(); }
}
