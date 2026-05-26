package com.sinapipro.api.registry.domain;

import com.sinapipro.api.shared.domain.TenantAwareEntity;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "project_settings", uniqueConstraints = @UniqueConstraint(columnNames = {"project_id", "key"}))
public class ProjectSettings extends TenantAwareEntity {
    @Column(name = "project_id", nullable = false) private UUID projectId;
    @Column(name = "key", nullable = false, length = 100) private String key;
    @Column(length = 500) private String value;
    @Column(length = 200) private String description;

    protected ProjectSettings() {}

    public ProjectSettings(UUID projectId, String key, String value, String description) {
        this.projectId = projectId; this.key = key; this.value = value; this.description = description;
    }

    public UUID getProjectId() { return projectId; }
    public String getKey() { return key; }
    public String getValue() { return value; }
    public String getDescription() { return description; }

    public void updateValue(String value) { this.value = value; }
}
