package com.sinapipro.api.registry.domain;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "payment_condition")
public class PaymentCondition {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(nullable = false, unique = true, length = 100) private String name;
    @Column(nullable = false) private int installments = 1;
    @Column(length = 500) private String description;
    @Column(nullable = false) private boolean active = true;
    protected PaymentCondition() {}
    public PaymentCondition(String name, int installments, String description) {
        this.name = name; this.installments = installments; this.description = description;
    }
    public UUID getId() { return id; }
    public String getName() { return name; }
    public int getInstallments() { return installments; }
    public String getDescription() { return description; }
    public boolean isActive() { return active; }
    public void deactivate() { this.active = false; }
    public void update(String name, int installments, String description) {
        this.name = name; this.installments = installments; this.description = description;
    }
}
