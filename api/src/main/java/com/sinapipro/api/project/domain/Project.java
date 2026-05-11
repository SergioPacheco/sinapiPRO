package com.sinapipro.api.project.domain;

import com.sinapipro.api.shared.domain.TenantAwareEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "project")
public class Project extends TenantAwareEntity {

    @Column(nullable = false, unique = true, length = 30)
    private String code;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(name = "customer_name", nullable = false, length = 200)
    private String customerName;

    @Column(name = "customer_document", length = 20)
    private String customerDocument;

    @Column(length = 300)
    private String address;

    @Column(length = 100)
    private String city;

    @Column(length = 2)
    private String state;

    @Column(name = "responsible_engineer", length = 200)
    private String responsibleEngineer;

    @Column(name = "art_number", length = 50)
    private String artNumber;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "expected_end_date")
    private LocalDate expectedEndDate;

    @Column(name = "actual_end_date")
    private LocalDate actualEndDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProjectStatus status = ProjectStatus.PLANNING;

    @Column(name = "total_area", precision = 14, scale = 2)
    private BigDecimal totalArea;

    @Column(name = "total_budget", precision = 18, scale = 2)
    private BigDecimal totalBudget;

    protected Project() {}

    public Project(String code, String name, String customerName) {
        this.code = code;
        this.name = name;
        this.customerName = customerName;
    }

    // Getters
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getCustomerName() { return customerName; }
    public String getCustomerDocument() { return customerDocument; }
    public String getAddress() { return address; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getResponsibleEngineer() { return responsibleEngineer; }
    public String getArtNumber() { return artNumber; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getExpectedEndDate() { return expectedEndDate; }
    public LocalDate getActualEndDate() { return actualEndDate; }
    public ProjectStatus getStatus() { return status; }
    public BigDecimal getTotalArea() { return totalArea; }
    public BigDecimal getTotalBudget() { return totalBudget; }

    public void update(String name, String description, String customerName, String customerDocument,
                       String address, String city, String state, String responsibleEngineer,
                       String artNumber, LocalDate startDate, LocalDate expectedEndDate,
                       BigDecimal totalArea, BigDecimal totalBudget) {
        this.name = name;
        this.description = description;
        this.customerName = customerName;
        this.customerDocument = customerDocument;
        this.address = address;
        this.city = city;
        this.state = state;
        this.responsibleEngineer = responsibleEngineer;
        this.artNumber = artNumber;
        this.startDate = startDate;
        this.expectedEndDate = expectedEndDate;
        this.totalArea = totalArea;
        this.totalBudget = totalBudget;
    }

    public void updateStatus(ProjectStatus status) {
        this.status = status;
        if (status == ProjectStatus.COMPLETED) {
            this.actualEndDate = LocalDate.now();
        }
    }
}
