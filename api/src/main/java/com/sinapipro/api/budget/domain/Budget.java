package com.sinapipro.api.budget.domain;

import module java.base;

import com.sinapipro.api.shared.domain.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "budget")
public class Budget extends AuditableEntity {

    @Column(nullable = false, unique = true, length = 40)
    private String code;

    @Column(nullable = false, length = 140)
    private String title;

    @Column(name = "customer_name", nullable = false, length = 140)
    private String customerName;

    @Column(name = "total_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private BudgetStatus status;

    @Column(nullable = false)
    private boolean active = false;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private Map<String, Object> metadata = Map.of();

    @Column(name = "project_id")
    private UUID projectId;

    @Column(name = "reference_date")
    private LocalDate referenceDate;

    @Column(name = "state", length = 2)
    private String state;

    @Column(name = "rounding_method", length = 20)
    private String roundingMethod = "TRUNCATE";

    @Column(name = "decimal_places")
    private Integer decimalPlaces = 4;

    protected Budget() {}

    public Budget(String code, String title, String customerName, BigDecimal totalAmount,
                  BudgetStatus status, LocalDate startDate, LocalDate endDate, Map<String, Object> metadata) {
        this.code = code;
        this.title = title;
        this.customerName = customerName;
        this.totalAmount = totalAmount;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.metadata = metadata != null ? metadata : Map.of();
    }

    public String getCode() { return code; }
    public String getTitle() { return title; }
    public String getCustomerName() { return customerName; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public BudgetStatus getStatus() { return status; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public void setStatus(BudgetStatus status) { this.status = status; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public Map<String, Object> getMetadata() { return metadata; }

    public UUID getProjectId() { return projectId; }
    public void setProjectId(UUID projectId) { this.projectId = projectId; }
    public LocalDate getReferenceDate() { return referenceDate; }
    public void setReferenceDate(LocalDate referenceDate) { this.referenceDate = referenceDate; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getRoundingMethod() { return roundingMethod; }
    public void setRoundingMethod(String roundingMethod) { this.roundingMethod = roundingMethod; }
    public Integer getDecimalPlaces() { return decimalPlaces; }
    public void setDecimalPlaces(Integer decimalPlaces) { this.decimalPlaces = decimalPlaces; }

    public void update(String title, String customerName, BigDecimal totalAmount,
                       BudgetStatus status, LocalDate startDate, LocalDate endDate, Map<String, Object> metadata) {
        this.title = title;
        this.customerName = customerName;
        this.totalAmount = totalAmount;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.metadata = metadata != null ? metadata : Map.of();
    }
}
