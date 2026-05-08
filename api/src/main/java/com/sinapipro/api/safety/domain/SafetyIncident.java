package com.sinapipro.api.safety.domain;

import com.sinapipro.api.shared.domain.AuditableEntity;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "safety_incident")
public class SafetyIncident extends AuditableEntity {

    @Column(name = "budget_id", nullable = false)
    private UUID budgetId;

    @Column(name = "incident_date", nullable = false)
    private LocalDate incidentDate;

    @Column(nullable = false, length = 20)
    private String severity; // LOW, MEDIUM, HIGH, CRITICAL

    @Column(nullable = false, columnDefinition = "text")
    private String description;

    @Column(length = 200)
    private String location;

    @Column(name = "injured_party", length = 140)
    private String injuredParty;

    @Column(name = "corrective_action", columnDefinition = "text")
    private String correctiveAction;

    @Column(name = "reported_by", length = 140)
    private String reportedBy;

    @Column(nullable = false, length = 20)
    private String status; // OPEN, INVESTIGATING, RESOLVED

    protected SafetyIncident() {}

    public SafetyIncident(UUID budgetId, LocalDate incidentDate, String severity, String description,
                          String location, String injuredParty, String reportedBy) {
        this.budgetId = budgetId;
        this.incidentDate = incidentDate;
        this.severity = severity;
        this.description = description;
        this.location = location;
        this.injuredParty = injuredParty;
        this.reportedBy = reportedBy;
        this.status = "OPEN";
    }

    public UUID getBudgetId() { return budgetId; }
    public LocalDate getIncidentDate() { return incidentDate; }
    public String getSeverity() { return severity; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public String getInjuredParty() { return injuredParty; }
    public String getCorrectiveAction() { return correctiveAction; }
    public String getReportedBy() { return reportedBy; }
    public String getStatus() { return status; }

    public void resolve(String correctiveAction) {
        this.correctiveAction = correctiveAction;
        this.status = "RESOLVED";
    }
}
