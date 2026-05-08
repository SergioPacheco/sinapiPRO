package com.sinapipro.api.rfi.domain;

import com.sinapipro.api.shared.domain.AuditableEntity;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "rfi", uniqueConstraints = @UniqueConstraint(columnNames = {"budget_id", "number"}))
public class Rfi extends AuditableEntity {

    @Column(name = "budget_id", nullable = false)
    private UUID budgetId;

    @Column(nullable = false)
    private Integer number;

    @Column(nullable = false, length = 300)
    private String subject;

    @Column(nullable = false, columnDefinition = "text")
    private String question;

    @Column(columnDefinition = "text")
    private String answer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RfiStatus status;

    @Column(nullable = false, length = 20)
    private String priority;

    @Column(name = "assigned_to", length = 140)
    private String assignedTo;

    @Column(name = "created_by", length = 140)
    private String createdBy;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "answered_at")
    private Instant answeredAt;

    protected Rfi() {}

    public Rfi(UUID budgetId, Integer number, String subject, String question,
               String priority, String assignedTo, String createdBy, LocalDate dueDate) {
        this.budgetId = budgetId;
        this.number = number;
        this.subject = subject;
        this.question = question;
        this.priority = priority;
        this.assignedTo = assignedTo;
        this.createdBy = createdBy;
        this.dueDate = dueDate;
        this.status = RfiStatus.OPEN;
    }

    public UUID getBudgetId() { return budgetId; }
    public Integer getNumber() { return number; }
    public String getSubject() { return subject; }
    public String getQuestion() { return question; }
    public String getAnswer() { return answer; }
    public RfiStatus getStatus() { return status; }
    public String getPriority() { return priority; }
    public String getAssignedTo() { return assignedTo; }
    public String getCreatedBy() { return createdBy; }
    public LocalDate getDueDate() { return dueDate; }
    public Instant getAnsweredAt() { return answeredAt; }

    public void respond(String answer) {
        if (this.status != RfiStatus.OPEN) throw new IllegalStateException("Can only answer OPEN RFIs");
        this.answer = answer;
        this.status = RfiStatus.ANSWERED;
        this.answeredAt = Instant.now();
    }

    public void close() {
        if (this.status != RfiStatus.ANSWERED) throw new IllegalStateException("Can only close ANSWERED RFIs");
        this.status = RfiStatus.CLOSED;
    }

    public boolean isOverdue() {
        return status == RfiStatus.OPEN && dueDate != null && LocalDate.now().isAfter(dueDate);
    }
}
