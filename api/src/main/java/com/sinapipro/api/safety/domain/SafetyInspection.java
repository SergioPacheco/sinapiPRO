package com.sinapipro.api.safety.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "safety_inspection")
public class SafetyInspection {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "budget_id", nullable = false)
    private UUID budgetId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", nullable = false)
    private SafetyChecklistTemplate template;

    @Column(nullable = false, length = 140)
    private String inspector;

    @Column(name = "inspection_date", nullable = false)
    private LocalDate inspectionDate;

    @Column(nullable = false, length = 20)
    private String status; // PASS, FAIL, PARTIAL

    @Column(nullable = false, columnDefinition = "jsonb")
    private String results; // JSON array of item results

    @Column(columnDefinition = "text")
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist void prePersist() { createdAt = Instant.now(); }

    protected SafetyInspection() {}

    public SafetyInspection(UUID budgetId, SafetyChecklistTemplate template, String inspector,
                            LocalDate inspectionDate, String status, String results, String notes) {
        this.budgetId = budgetId;
        this.template = template;
        this.inspector = inspector;
        this.inspectionDate = inspectionDate;
        this.status = status;
        this.results = results;
        this.notes = notes;
    }

    public UUID getId() { return id; }
    public UUID getBudgetId() { return budgetId; }
    public SafetyChecklistTemplate getTemplate() { return template; }
    public String getInspector() { return inspector; }
    public LocalDate getInspectionDate() { return inspectionDate; }
    public String getStatus() { return status; }
    public String getResults() { return results; }
    public String getNotes() { return notes; }
}
