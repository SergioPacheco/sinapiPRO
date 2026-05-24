package com.sinapipro.api.timetracking.domain;
import com.sinapipro.api.shared.domain.TenantAwareEntity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "salary_history")
public class SalaryHistory extends TenantAwareEntity {
    @Column(name = "employee_id", nullable = false) private UUID employeeId;
    @Column(name = "effective_date", nullable = false) private LocalDate effectiveDate;
    @Column(nullable = false, precision = 14, scale = 2) private BigDecimal salary;
    @Column(name = "hourly_rate", nullable = false, precision = 14, scale = 4) private BigDecimal hourlyRate;
    @Column(length = 200) private String reason;

    protected SalaryHistory() {}

    public SalaryHistory(UUID employeeId, LocalDate effectiveDate, BigDecimal salary,
                          BigDecimal hourlyRate, String reason) {
        this.employeeId = employeeId;
        this.effectiveDate = effectiveDate;
        this.salary = salary;
        this.hourlyRate = hourlyRate;
        this.reason = reason;
    }

    public UUID getEmployeeId() { return employeeId; }
    public LocalDate getEffectiveDate() { return effectiveDate; }
    public BigDecimal getSalary() { return salary; }
    public BigDecimal getHourlyRate() { return hourlyRate; }
    public String getReason() { return reason; }
}
