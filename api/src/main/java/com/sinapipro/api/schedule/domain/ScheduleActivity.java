package com.sinapipro.api.schedule.domain;

import com.sinapipro.api.budget.domain.Budget;
import com.sinapipro.api.shared.domain.AuditableEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "schedule_activity")
public class ScheduleActivity extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "budget_id", nullable = false)
    private Budget budget;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(name = "planned_start", nullable = false)
    private LocalDate plannedStart;

    @Column(name = "planned_end", nullable = false)
    private LocalDate plannedEnd;

    @Column(name = "actual_start")
    private LocalDate actualStart;

    @Column(name = "actual_end")
    private LocalDate actualEnd;

    @Column(nullable = false, precision = 6, scale = 4)
    private BigDecimal weight;

    @Column(name = "progress_pct", nullable = false, precision = 5, scale = 2)
    private BigDecimal progressPct;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    protected ScheduleActivity() {}

    public ScheduleActivity(Budget budget, String name, LocalDate plannedStart, LocalDate plannedEnd,
                            BigDecimal weight, Integer sortOrder) {
        this.budget = budget;
        this.name = name;
        this.plannedStart = plannedStart;
        this.plannedEnd = plannedEnd;
        this.weight = weight;
        this.progressPct = BigDecimal.ZERO;
        this.sortOrder = sortOrder;
    }

    public Budget getBudget() { return budget; }
    public String getName() { return name; }
    public LocalDate getPlannedStart() { return plannedStart; }
    public LocalDate getPlannedEnd() { return plannedEnd; }
    public LocalDate getActualStart() { return actualStart; }
    public LocalDate getActualEnd() { return actualEnd; }
    public BigDecimal getWeight() { return weight; }
    public BigDecimal getProgressPct() { return progressPct; }
    public Integer getSortOrder() { return sortOrder; }

    public void updateProgress(BigDecimal progressPct, LocalDate actualStart, LocalDate actualEnd) {
        this.progressPct = progressPct;
        this.actualStart = actualStart;
        this.actualEnd = actualEnd;
    }
}
