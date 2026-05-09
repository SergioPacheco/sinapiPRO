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

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private Map<String, Object> metadata = Map.of();

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
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public Map<String, Object> getMetadata() { return metadata; }

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
