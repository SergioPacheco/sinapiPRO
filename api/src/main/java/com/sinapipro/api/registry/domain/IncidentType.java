package com.sinapipro.api.registry.domain;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "incident_type")
public class IncidentType {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(nullable = false, unique = true, length = 100) private String name;
    @Column(nullable = false, length = 20) private String severity;
    @Column(length = 500) private String description;
    protected IncidentType() {}
    public IncidentType(String name, String severity, String description) {
        this.name = name; this.severity = severity; this.description = description;
    }
    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getSeverity() { return severity; }
    public String getDescription() { return description; }
    public void update(String name, String severity, String description) {
        this.name = name; this.severity = severity; this.description = description;
    }
}
