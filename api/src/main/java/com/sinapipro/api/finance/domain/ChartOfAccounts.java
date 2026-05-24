package com.sinapipro.api.finance.domain;
import com.sinapipro.api.shared.domain.TenantAwareEntity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "chart_of_accounts")
public class ChartOfAccounts extends TenantAwareEntity {
    @Column(nullable = false, unique = true, length = 20) private String code;
    @Column(nullable = false, length = 200) private String name;
    @Column(name = "parent_id") private UUID parentId;
    @Column(nullable = false, length = 20) private String type;
    @Column(nullable = false) private int level = 1;
    @Column(nullable = false) private boolean active = true;

    protected ChartOfAccounts() {}
    public ChartOfAccounts(String code, String name, UUID parentId, String type, int level) {
        this.code = code; this.name = name; this.parentId = parentId; this.type = type; this.level = level;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public UUID getParentId() { return parentId; }
    public String getType() { return type; }
    public int getLevel() { return level; }
    public boolean isActive() { return active; }
}
