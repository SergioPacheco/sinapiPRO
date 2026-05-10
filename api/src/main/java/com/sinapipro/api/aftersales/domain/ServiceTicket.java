package com.sinapipro.api.aftersales.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "service_ticket")
public class ServiceTicket {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(name = "unit_id") private UUID unitId;
    @Column(name = "client_name", nullable = false, length = 200) private String clientName;
    @Column(nullable = false, length = 60) private String category;
    @Column(nullable = false, columnDefinition = "text") private String description;
    @Column(nullable = false, length = 20) private String priority;
    @Column(nullable = false, length = 20) private String status;
    @Column(name = "assigned_to", length = 140) private String assignedTo;
    @Column(columnDefinition = "text") private String resolution;
    @Column(name = "opened_at", nullable = false, updatable = false) private Instant openedAt;
    @Column(name = "due_date") private LocalDate dueDate;
    @Column(name = "resolved_at") private Instant resolvedAt;
    @Column(name = "closed_at") private Instant closedAt;

    @PrePersist void prePersist() { openedAt = Instant.now(); }

    protected ServiceTicket() {}
    public ServiceTicket(UUID unitId, String clientName, String category, String description, String priority, LocalDate dueDate) {
        this.unitId = unitId; this.clientName = clientName; this.category = category;
        this.description = description; this.priority = priority; this.status = "OPEN"; this.dueDate = dueDate;
    }

    public UUID getId() { return id; }
    public UUID getUnitId() { return unitId; }
    public String getClientName() { return clientName; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public String getPriority() { return priority; }
    public String getStatus() { return status; }
    public String getAssignedTo() { return assignedTo; }
    public String getResolution() { return resolution; }
    public Instant getOpenedAt() { return openedAt; }
    public LocalDate getDueDate() { return dueDate; }
    public Instant getResolvedAt() { return resolvedAt; }
    public Instant getClosedAt() { return closedAt; }

    public void assign(String assignedTo) { this.assignedTo = assignedTo; this.status = "IN_PROGRESS"; }
    public void resolve(String resolution) { this.resolution = resolution; this.resolvedAt = Instant.now(); this.status = "RESOLVED"; }
    public void close() { this.closedAt = Instant.now(); this.status = "CLOSED"; }
    public void reopen() { this.status = "OPEN"; this.resolvedAt = null; this.closedAt = null; }
}
