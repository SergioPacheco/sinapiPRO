package com.sinapipro.api.registry.domain;
import com.sinapipro.api.shared.domain.TenantAwareEntity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "social_charge")
public class SocialCharge extends TenantAwareEntity {
    @Column(nullable = false, length = 100) private String name;
    @Column(nullable = false, length = 20) private String type;
    @Column(nullable = false) private BigDecimal percentage = BigDecimal.ZERO;
    protected SocialCharge() {}
    public SocialCharge(String name, String type, BigDecimal percentage) {
        this.name = name; this.type = type; this.percentage = percentage;
    }
    public String getName() { return name; }
    public String getType() { return type; }
    public BigDecimal getPercentage() { return percentage; }
    public void update(String name, String type, BigDecimal percentage) {
        this.name = name; this.type = type; this.percentage = percentage;
    }
}
