package com.sinapipro.api.registry.domain;
import com.sinapipro.api.shared.domain.TenantAwareEntity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "payment_method")
public class PaymentMethod extends TenantAwareEntity {
    @Column(nullable = false, unique = true, length = 100) private String name;
    @Column(nullable = false) private int installments = 1;
    @Column(nullable = false) private boolean active = true;
    protected PaymentMethod() {}
    public PaymentMethod(String name, int installments) { this.name = name; this.installments = installments; }
    public String getName() { return name; }
    public int getInstallments() { return installments; }
    public boolean isActive() { return active; }
    public void deactivate() { this.active = false; }
}
