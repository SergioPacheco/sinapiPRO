package com.sinapipro.api.submittal.domain;

import com.sinapipro.api.shared.domain.AuditableEntity;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "submittal", uniqueConstraints = @UniqueConstraint(columnNames = {"budget_id", "number"}))
public class Submittal extends AuditableEntity {

    @Column(name = "budget_id", nullable = false)
    private UUID budgetId;
    @Column(nullable = false) private Integer number;
    @Column(nullable = false, length = 300) private String title;
    @Column(name = "spec_section", length = 60) private String specSection;
    @Column(nullable = false, length = 40) private String type; // SHOP_DRAWING, SAMPLE, PRODUCT_DATA, MOCK_UP
    @Column(name = "submitted_by", length = 140) private String submittedBy;
    @Column(name = "assigned_to", length = 140) private String assignedTo;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20) private SubmittalStatus status;
    @Column(name = "due_date") private LocalDate dueDate;
    @Column(name = "submitted_at") private Instant submittedAt;
    @Column(name = "reviewed_at") private Instant reviewedAt;
    @Column(name = "reviewer_notes", columnDefinition = "text") private String reviewerNotes;

    protected Submittal() {}

    public Submittal(UUID budgetId, Integer number, String title, String specSection, String type,
                     String submittedBy, String assignedTo, LocalDate dueDate) {
        this.budgetId = budgetId; this.number = number; this.title = title;
        this.specSection = specSection; this.type = type; this.submittedBy = submittedBy;
        this.assignedTo = assignedTo; this.dueDate = dueDate; this.status = SubmittalStatus.DRAFT;
    }

    public UUID getBudgetId() { return budgetId; }
    public Integer getNumber() { return number; }
    public String getTitle() { return title; }
    public String getSpecSection() { return specSection; }
    public String getType() { return type; }
    public String getSubmittedBy() { return submittedBy; }
    public String getAssignedTo() { return assignedTo; }
    public SubmittalStatus getStatus() { return status; }
    public LocalDate getDueDate() { return dueDate; }
    public Instant getSubmittedAt() { return submittedAt; }
    public Instant getReviewedAt() { return reviewedAt; }
    public String getReviewerNotes() { return reviewerNotes; }

    public void submit() {
        if (status != SubmittalStatus.DRAFT) throw new IllegalStateException("Can only submit DRAFT submittals");
        this.status = SubmittalStatus.SUBMITTED; this.submittedAt = Instant.now();
    }
    public void approve(String notes) {
        if (status != SubmittalStatus.SUBMITTED) throw new IllegalStateException("Can only approve SUBMITTED");
        this.status = SubmittalStatus.APPROVED; this.reviewedAt = Instant.now(); this.reviewerNotes = notes;
    }
    public void approveAsNoted(String notes) {
        if (status != SubmittalStatus.SUBMITTED) throw new IllegalStateException("Can only review SUBMITTED");
        this.status = SubmittalStatus.APPROVED_AS_NOTED; this.reviewedAt = Instant.now(); this.reviewerNotes = notes;
    }
    public void reject(String notes) {
        if (status != SubmittalStatus.SUBMITTED) throw new IllegalStateException("Can only reject SUBMITTED");
        this.status = SubmittalStatus.REJECTED; this.reviewedAt = Instant.now(); this.reviewerNotes = notes;
    }
    public void reviseAndResubmit(String notes) {
        if (status != SubmittalStatus.SUBMITTED) throw new IllegalStateException("Can only revise SUBMITTED");
        this.status = SubmittalStatus.REVISE_RESUBMIT; this.reviewedAt = Instant.now(); this.reviewerNotes = notes;
    }

    public void revise() {
        if (status != SubmittalStatus.REVISE_RESUBMIT && status != SubmittalStatus.REJECTED)
            throw new IllegalStateException("Can only revise REJECTED or REVISE_RESUBMIT submittals");
        this.status = SubmittalStatus.DRAFT;
        this.reviewerNotes = null;
        this.reviewedAt = null;
    }
}
