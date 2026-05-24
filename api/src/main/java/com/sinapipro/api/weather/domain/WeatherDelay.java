package com.sinapipro.api.weather.domain;
import com.sinapipro.api.shared.domain.TenantAwareEntity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "weather_delay", uniqueConstraints = @UniqueConstraint(columnNames = {"budget_id", "delay_date"}))
public class WeatherDelay extends TenantAwareEntity {

    @Column(name = "budget_id", nullable = false) private UUID budgetId;
    @Column(name = "delay_date", nullable = false) private LocalDate delayDate;
    @Column(name = "weather_condition", nullable = false, length = 60) private String weatherCondition;
    @Column(name = "hours_lost", nullable = false, precision = 4, scale = 2) private BigDecimal hoursLost;
    @Column(name = "full_day_lost", nullable = false) private Boolean fullDayLost;
    @Column(name = "impact_description", length = 500) private String impactDescription;
    @Column(name = "reported_by", length = 140) private String reportedBy;

    protected WeatherDelay() {}

    public WeatherDelay(UUID budgetId, LocalDate delayDate, String weatherCondition, BigDecimal hoursLost,
                        Boolean fullDayLost, String impactDescription, String reportedBy) {
        this.budgetId = budgetId; this.delayDate = delayDate; this.weatherCondition = weatherCondition;
        this.hoursLost = hoursLost; this.fullDayLost = fullDayLost;
        this.impactDescription = impactDescription; this.reportedBy = reportedBy;
    }

    public UUID getBudgetId() { return budgetId; }
    public LocalDate getDelayDate() { return delayDate; }
    public String getWeatherCondition() { return weatherCondition; }
    public BigDecimal getHoursLost() { return hoursLost; }
    public Boolean getFullDayLost() { return fullDayLost; }
    public String getImpactDescription() { return impactDescription; }
    public String getReportedBy() { return reportedBy; }
}
