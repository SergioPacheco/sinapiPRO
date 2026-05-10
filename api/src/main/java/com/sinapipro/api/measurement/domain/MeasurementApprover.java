package com.sinapipro.api.measurement.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "measurement_approver")
public class MeasurementApprover {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(nullable = false, length = 140)
    private String name;

    @Column(length = 200)
    private String email;

    @Column(nullable = false, length = 30)
    private String role = "FISCAL"; // FISCAL, SUPERVISOR, ENGINEER

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public MeasurementApprover() {}

    public MeasurementApprover(UUID projectId, String name, String email, String role) {
        this.projectId = projectId;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public UUID getId() { return id; }
    public UUID getProjectId() { return projectId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public boolean isActive() { return active; }
}
