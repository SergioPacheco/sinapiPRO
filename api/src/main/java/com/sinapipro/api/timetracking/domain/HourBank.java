package com.sinapipro.api.timetracking.domain;
import com.sinapipro.api.shared.domain.TenantAwareEntity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "hour_bank")
public class HourBank extends TenantAwareEntity {
    @Column(name = "employee_id", nullable = false) private UUID employeeId;
    @Column(name = "project_id", nullable = false) private UUID projectId;
    @Column(name = "competency_id", nullable = false) private UUID competencyId;
    @Column(nullable = false, length = 10) private String type; // CREDIT, DEBIT
    @Column(nullable = false, precision = 8, scale = 2) private BigDecimal hours;
    @Column(length = 200) private String description;
    @Column(name = "reference_date", nullable = false) private LocalDate referenceDate;

    protected HourBank() {}

    public HourBank(UUID employeeId, UUID projectId, UUID competencyId, String type,
                    BigDecimal hours, String description, LocalDate referenceDate) {
        this.employeeId = employeeId;
        this.projectId = projectId;
        this.competencyId = competencyId;
        this.type = type;
        this.hours = hours;
        this.description = description;
        this.referenceDate = referenceDate;
    }

    public UUID getEmployeeId() { return employeeId; }
    public UUID getProjectId() { return projectId; }
    public UUID getCompetencyId() { return competencyId; }
    public String getType() { return type; }
    public BigDecimal getHours() { return hours; }
    public String getDescription() { return description; }
    public LocalDate getReferenceDate() { return referenceDate; }
}
