package com.sinapipro.api.timetracking.domain;

import com.sinapipro.api.shared.domain.TenantAwareEntity;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "competency_period", uniqueConstraints = @UniqueConstraint(columnNames = {"project_id", "year_month"}))
public class CompetencyPeriod extends TenantAwareEntity {
    @Column(name = "project_id", nullable = false) private UUID projectId;
    @Column(name = "year_month", nullable = false) private LocalDate yearMonth;
    @Column(nullable = false, length = 20) private String status = "OPEN";
    @Column(name = "closed_at") private Instant closedAt;
    @Column(name = "closed_by", length = 200) private String closedBy;

    protected CompetencyPeriod() {}
    public CompetencyPeriod(UUID projectId, LocalDate yearMonth) {
        this.projectId = projectId;
        this.yearMonth = yearMonth;
    }

    public UUID getProjectId() { return projectId; }
    public LocalDate getYearMonth() { return yearMonth; }
    public String getStatus() { return status; }
    public Instant getClosedAt() { return closedAt; }
    public String getClosedBy() { return closedBy; }
    public boolean isOpen() { return "OPEN".equals(status); }

    public void close(String closedBy) {
        this.status = "CLOSED";
        this.closedAt = Instant.now();
        this.closedBy = closedBy;
    }

    public void reopen() {
        this.status = "OPEN";
        this.closedAt = null;
        this.closedBy = null;
    }
}
