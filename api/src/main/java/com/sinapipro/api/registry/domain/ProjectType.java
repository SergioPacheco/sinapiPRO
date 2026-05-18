package com.sinapipro.api.registry.domain;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "project_type")
public class ProjectType {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(nullable = false, unique = true, length = 100) private String name;
    @Column(length = 500) private String description;
    protected ProjectType() {}
    public ProjectType(String name, String description) { this.name = name; this.description = description; }
    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public void update(String name, String description) { this.name = name; this.description = description; }
}
