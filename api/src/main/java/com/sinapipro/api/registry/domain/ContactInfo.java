package com.sinapipro.api.registry.domain;

import com.sinapipro.api.shared.domain.TenantAwareEntity;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "contact_info")
public class ContactInfo extends TenantAwareEntity {
    @Column(name = "entity_type", nullable = false, length = 30) private String entityType;
    @Column(name = "entity_id", nullable = false) private UUID entityId;
    @Column(name = "info_type", nullable = false, length = 20) private String infoType;
    @Column(nullable = false, length = 30) private String label;
    @Column(nullable = false, length = 400) private String value;
    @Column(name = "is_primary", nullable = false) private boolean primary = false;

    protected ContactInfo() {}

    public ContactInfo(String entityType, UUID entityId, String infoType, String label, String value, boolean primary) {
        this.entityType = entityType; this.entityId = entityId; this.infoType = infoType;
        this.label = label; this.value = value; this.primary = primary;
    }

    public String getEntityType() { return entityType; }
    public UUID getEntityId() { return entityId; }
    public String getInfoType() { return infoType; }
    public String getLabel() { return label; }
    public String getValue() { return value; }
    public boolean isPrimary() { return primary; }

    public void update(String infoType, String label, String value, boolean primary) {
        this.infoType = infoType; this.label = label; this.value = value; this.primary = primary;
    }
}
