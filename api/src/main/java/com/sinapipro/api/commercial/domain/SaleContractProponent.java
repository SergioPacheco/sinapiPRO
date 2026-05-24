package com.sinapipro.api.commercial.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "sale_contract_proponent")
public class SaleContractProponent {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(name = "contract_id", nullable = false) private UUID contractId;
    @Column(name = "client_id", nullable = false) private UUID clientId;
    @Column(name = "participation_pct", nullable = false, precision = 5, scale = 2) private BigDecimal participationPct;
    @Column(nullable = false, length = 20) private String role = "BUYER";

    protected SaleContractProponent() {}
    public SaleContractProponent(UUID contractId, UUID clientId, BigDecimal participationPct, String role) {
        this.contractId = contractId; this.clientId = clientId;
        this.participationPct = participationPct; this.role = role;
    }

    public UUID getId() { return id; }
    public UUID getContractId() { return contractId; }
    public UUID getClientId() { return clientId; }
    public BigDecimal getParticipationPct() { return participationPct; }
    public String getRole() { return role; }
}
