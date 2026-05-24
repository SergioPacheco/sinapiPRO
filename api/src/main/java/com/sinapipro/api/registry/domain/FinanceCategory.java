package com.sinapipro.api.registry.domain;
import com.sinapipro.api.shared.domain.TenantAwareEntity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "finance_category")
public class FinanceCategory extends TenantAwareEntity {
    @Column(nullable = false, unique = true, length = 30) private String code;
    @Column(nullable = false, length = 200) private String name;
    @Column(nullable = false, length = 20) private String type;
    @Column(nullable = false) private boolean active = true;
    protected FinanceCategory() {}
    public FinanceCategory(String code, String name, String type) {
        this.code = code; this.name = name; this.type = type;
    }
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getType() { return type; }
    public boolean isActive() { return active; }
    public void deactivate() { this.active = false; }
    public void update(String code, String name, String type) {
        this.code = code; this.name = name; this.type = type;
    }
}
