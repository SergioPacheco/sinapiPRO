package com.sinapipro.api.registry.domain;
import com.sinapipro.api.shared.domain.TenantAwareEntity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "default_stage")
public class DefaultStage extends TenantAwareEntity {
    @Column(nullable = false, length = 100) private String name;
    @Column(name = "sort_order", nullable = false) private int sortOrder;
    @Column(length = 500) private String description;
    protected DefaultStage() {}
    public DefaultStage(String name, int sortOrder, String description) {
        this.name = name; this.sortOrder = sortOrder; this.description = description;
    }
    public String getName() { return name; }
    public int getSortOrder() { return sortOrder; }
    public String getDescription() { return description; }
    public void update(String name, int sortOrder, String description) {
        this.name = name; this.sortOrder = sortOrder; this.description = description;
    }
}
