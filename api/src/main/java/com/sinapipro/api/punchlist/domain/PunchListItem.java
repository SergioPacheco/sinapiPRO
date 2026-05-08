package com.sinapipro.api.punchlist.domain;

import com.sinapipro.api.shared.domain.AuditableEntity;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "punch_list_item")
public class PunchListItem extends AuditableEntity {

    @Column(name = "budget_id", nullable = false)
    private UUID budgetId;

    @Column(nullable = false, length = 200)
    private String location;

    @Column(nullable = false, columnDefinition = "text")
    private String description;

    @Column(length = 60)
    private String category;

    @Column(nullable = false, length = 20)
    private String priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PunchListStatus status;

    @Column(name = "assigned_to", length = 140)
    private String assignedTo;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "created_by", length = 140)
    private String createdBy;

    protected PunchListItem() {}

    public PunchListItem(UUID budgetId, String location, String description, String category,
                         String priority, String assignedTo, LocalDate dueDate, String createdBy) {
        this.budgetId = budgetId;
        this.location = location;
        this.description = description;
        this.category = category;
        this.priority = priority != null ? priority : "NORMAL";
        this.assignedTo = assignedTo;
        this.dueDate = dueDate;
        this.createdBy = createdBy;
        this.status = PunchListStatus.OPEN;
    }

    public UUID getBudgetId() { return budgetId; }
    public String getLocation() { return location; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public String getPriority() { return priority; }
    public PunchListStatus getStatus() { return status; }
    public String getAssignedTo() { return assignedTo; }
    public LocalDate getDueDate() { return dueDate; }
    public Instant getCompletedAt() { return completedAt; }
    public String getCreatedBy() { return createdBy; }

    public void markInProgress() {
        if (status != PunchListStatus.OPEN) throw new IllegalStateException("Can only start OPEN items");
        this.status = PunchListStatus.IN_PROGRESS;
    }

    public void complete() {
        if (status != PunchListStatus.OPEN && status != PunchListStatus.IN_PROGRESS)
            throw new IllegalStateException("Can only complete OPEN or IN_PROGRESS items");
        this.status = PunchListStatus.COMPLETED;
        this.completedAt = Instant.now();
    }

    public void verify() {
        if (status != PunchListStatus.COMPLETED) throw new IllegalStateException("Can only verify COMPLETED items");
        this.status = PunchListStatus.VERIFIED;
    }
}
