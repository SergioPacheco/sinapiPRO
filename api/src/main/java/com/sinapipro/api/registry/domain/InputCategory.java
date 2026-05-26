package com.sinapipro.api.registry.domain;

import com.sinapipro.api.shared.domain.TenantAwareEntity;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "input_category")
public class InputCategory extends TenantAwareEntity {
    @Column(nullable = false, length = 20) private String code;
    @Column(nullable = false, length = 200) private String name;
    @Column(name = "parent_id") private UUID parentId;
    @Column(nullable = false) private int level = 1;
    @Column(nullable = false) private boolean active = true;

    protected InputCategory() {}

    public InputCategory(String code, String name, UUID parentId, int level) {
        this.code = code; this.name = name; this.parentId = parentId; this.level = level;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public UUID getParentId() { return parentId; }
    public int getLevel() { return level; }
    public boolean isActive() { return active; }

    public void deactivate() { this.active = false; }

    public void update(String code, String name, UUID parentId, int level) {
        this.code = code; this.name = name; this.parentId = parentId; this.level = level;
    }
}
