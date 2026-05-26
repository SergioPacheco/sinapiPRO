package com.sinapipro.api.aftersales.domain;

import com.sinapipro.api.shared.domain.TenantAwareEntity;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name = "service_ticket_task")
public class ServiceTicketTask extends TenantAwareEntity {
    @Column(name = "ticket_id", nullable = false) private UUID ticketId;
    @Column(nullable = false, length = 300) private String description;
    @Column(name = "sort_order", nullable = false) private int sortOrder;
    @Column(nullable = false) private boolean completed = false;
    @Column(name = "completed_at") private Instant completedAt;
    @Column(name = "completed_by", length = 140) private String completedBy;

    protected ServiceTicketTask() {}
    public ServiceTicketTask(UUID ticketId, String description, int sortOrder) {
        this.ticketId = ticketId; this.description = description; this.sortOrder = sortOrder;
    }

    public UUID getTicketId() { return ticketId; }
    public String getDescription() { return description; }
    public int getSortOrder() { return sortOrder; }
    public boolean isCompleted() { return completed; }
    public Instant getCompletedAt() { return completedAt; }
    public String getCompletedBy() { return completedBy; }

    public void complete(String by) { this.completed = true; this.completedAt = Instant.now(); this.completedBy = by; }
    public void uncomplete() { this.completed = false; this.completedAt = null; this.completedBy = null; }
}
