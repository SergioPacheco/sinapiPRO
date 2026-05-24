package com.sinapipro.api.registry.domain;
import com.sinapipro.api.shared.domain.TenantAwareEntity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "cost_center")
public class CostCenter extends TenantAwareEntity {
    @Column(nullable = false, unique = true, length = 30) private String code;
    @Column(nullable = false, length = 200) private String name;
    @Column(length = 500) private String description;
    @Column(nullable = false) private boolean active = true;
    protected CostCenter() {}
    public CostCenter(String code, String name, String description) {
        this.code = code; this.name = name; this.description = description;
    }
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public boolean isActive() { return active; }
    public void deactivate() { this.active = false; }
    public void update(String code, String name, String description) {
        this.code = code; this.name = name; this.description = description;
    }
}
