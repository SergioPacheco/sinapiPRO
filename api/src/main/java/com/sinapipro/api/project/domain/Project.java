package com.sinapipro.api.project.domain;

import com.sinapipro.api.shared.domain.ContractRegime;
import com.sinapipro.api.shared.domain.ProjectType;
import com.sinapipro.api.shared.domain.TenantAwareEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

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

    @Column(name = "client_id")
    private UUID clientId;

    @Column(name = "employee_id")
    private UUID employeeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "project_type", length = 30)
    private ProjectType projectType;

    @Enumerated(EnumType.STRING)
    @Column(name = "contract_regime", length = 30)
    private ContractRegime contractRegime;

    @Column(name = "permit_number", length = 50)
    private String permitNumber;

    @Column(name = "permit_expiry")
    private LocalDate permitExpiry;

    @Column(name = "cei_cno", length = 30)
    private String ceiCno;

    @Column(name = "postal_code", length = 10)
    private String postalCode;

    @Column(length = 100)
    private String neighborhood;

    @Column(name = "address_number", length = 20)
    private String addressNumber;

    @Column(length = 30)
    private String phone;

    @Column(name = "total_built_area", precision = 14, scale = 2)
    private BigDecimal totalBuiltArea;

    @Column(name = "development_id")
    private UUID developmentId;

    @Column(name = "branch_id")
    private UUID branchId;

    @Column(name = "accounting_code", length = 30)
    private String accountingCode;

    @Column(name = "financial_control_enabled", nullable = false)
    private boolean financialControlEnabled;

    @Column(name = "stock_control_enabled", nullable = false)
    private boolean stockControlEnabled;

    @Column(name = "budget_control_enabled", nullable = false)
    private boolean budgetControlEnabled;

    @Column(name = "cost_apportionment_enabled", nullable = false)
    private boolean costApportionmentEnabled;

    @Column(name = "apportionment_rate", precision = 5, scale = 2)
    private BigDecimal apportionmentRate;

    @Column(name = "purchase_limit_no_auth", precision = 18, scale = 2)
    private BigDecimal purchaseLimitNoAuth;

    @Column(name = "billing_to_client", nullable = false)
    private boolean billingToClient;

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
    public UUID getClientId() { return clientId; }
    public UUID getEmployeeId() { return employeeId; }
    public ProjectType getProjectType() { return projectType; }
    public ContractRegime getContractRegime() { return contractRegime; }
    public String getPermitNumber() { return permitNumber; }
    public LocalDate getPermitExpiry() { return permitExpiry; }
    public String getCeiCno() { return ceiCno; }
    public String getPostalCode() { return postalCode; }
    public String getNeighborhood() { return neighborhood; }
    public String getAddressNumber() { return addressNumber; }
    public String getPhone() { return phone; }
    public BigDecimal getTotalBuiltArea() { return totalBuiltArea; }
    public UUID getDevelopmentId() { return developmentId; }
    public UUID getBranchId() { return branchId; }
    public String getAccountingCode() { return accountingCode; }
    public boolean isFinancialControlEnabled() { return financialControlEnabled; }
    public boolean isStockControlEnabled() { return stockControlEnabled; }
    public boolean isBudgetControlEnabled() { return budgetControlEnabled; }
    public boolean isCostApportionmentEnabled() { return costApportionmentEnabled; }
    public BigDecimal getApportionmentRate() { return apportionmentRate; }
    public BigDecimal getPurchaseLimitNoAuth() { return purchaseLimitNoAuth; }
    public boolean isBillingToClient() { return billingToClient; }

    public void update(String name, String description, String customerName, String customerDocument,
                       String address, String city, String state, String responsibleEngineer,
                       String artNumber, LocalDate startDate, LocalDate expectedEndDate,
                       BigDecimal totalArea, BigDecimal totalBudget,
                       UUID clientId, UUID employeeId, ProjectType projectType,
                       ContractRegime contractRegime, String permitNumber,
                       LocalDate permitExpiry, String ceiCno, String postalCode) {
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
        this.clientId = clientId;
        this.employeeId = employeeId;
        this.projectType = projectType;
        this.contractRegime = contractRegime;
        this.permitNumber = permitNumber;
        this.permitExpiry = permitExpiry;
        this.ceiCno = ceiCno;
        this.postalCode = postalCode;
    }

    public void updateStatus(ProjectStatus status) {
        this.status = status;
        if (status == ProjectStatus.COMPLETED) {
            this.actualEndDate = LocalDate.now();
        }
    }
}
