package com.sinapipro.api.safety.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "safety_checklist_template")
public class SafetyChecklistTemplate {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, length = 60)
    private String category;

    @Column(nullable = false, columnDefinition = "jsonb")
    private String items; // JSON array of checklist items

    @Column(nullable = false)
    private Boolean active;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist void prePersist() { createdAt = Instant.now(); }

    protected SafetyChecklistTemplate() {}

    public SafetyChecklistTemplate(String name, String category, String items) {
        this.name = name;
        this.category = category;
        this.items = items;
        this.active = true;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getItems() { return items; }
    public Boolean getActive() { return active; }
    public void deactivate() { this.active = false; }
}
