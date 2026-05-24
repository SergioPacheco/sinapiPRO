package com.sinapipro.api.registry.domain;

import com.sinapipro.api.shared.domain.TenantAwareEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "employee")
public class Employee extends TenantAwareEntity {
    @Column(name = "employee_code", nullable = false, unique = true, length = 30) private String employeeCode;
    @Column(nullable = false, length = 200) private String name;
    @Column(length = 20) private String document;
    @Column(nullable = false, length = 80) private String role;
    @Column(nullable = false, length = 100) private String specialty;
    @Column(nullable = false, length = 20) private String type; // EMPLOYEE, CONTRACTOR
    @Column(name = "employment_status", nullable = false, length = 20) private String employmentStatus;
    @Column(length = 200) private String email;
    @Column(length = 30) private String phone;
    @Column(name = "mobile_phone", length = 30) private String mobilePhone;
    @Column(name = "emergency_contact_name", length = 140) private String emergencyContactName;
    @Column(name = "emergency_contact_phone", length = 30) private String emergencyContactPhone;
    @Column(length = 300) private String address;
    @Column(length = 100) private String city;
    @Column(length = 2) private String state;
    @Column(name = "postal_code", length = 20) private String postalCode;
    @Column(name = "cost_center", length = 80) private String costCenter;
    @Column(name = "company_name", length = 140) private String companyName;
    @Column(length = 1000) private String notes;
    @Column(name = "hourly_rate", precision = 14, scale = 4) private BigDecimal hourlyRate;
    @Column(name = "admission_date") private LocalDate admissionDate;
    @Column(name = "termination_date") private LocalDate terminationDate;
    @Column(nullable = false) private boolean active = true;

    // V11 enrichment fields
    @Column(name = "birth_date") private LocalDate birthDate;
    @Column(length = 1) private String gender;
    @Column(name = "marital_status", length = 20) private String maritalStatus;
    @Column(length = 20) private String rg;
    @Column(name = "rg_issuer", length = 20) private String rgIssuer;
    @Column(length = 20) private String pis;
    @Column(length = 20) private String ctps;
    @Column(name = "ctps_series", length = 10) private String ctpsSeries;
    @Column(name = "bank_code", length = 10) private String bankCode;
    @Column(name = "bank_agency", length = 20) private String bankAgency;
    @Column(name = "bank_account_number", length = 30) private String bankAccountNumber;
    @Column(precision = 14, scale = 2) private BigDecimal salary;
    @Column(length = 100) private String department;
    @Column(name = "cost_center_id") private java.util.UUID costCenterId;
    @Column(name = "dismissal_date") private LocalDate dismissalDate;
    @Column(name = "dismissal_reason", length = 200) private String dismissalReason;

    protected Employee() {}
    public Employee(String employeeCode, String name, String document, String role, String specialty,
                    String type, String employmentStatus, String email, String phone, String mobilePhone,
                    String emergencyContactName, String emergencyContactPhone, String address, String city,
                    String state, String postalCode, String costCenter, String companyName, String notes,
                    BigDecimal hourlyRate, LocalDate admissionDate, LocalDate terminationDate) {
        this.employeeCode = employeeCode; this.name = name; this.document = document; this.role = role;
        this.specialty = specialty; this.type = type; this.employmentStatus = employmentStatus;
        this.email = email; this.phone = phone; this.mobilePhone = mobilePhone;
        this.emergencyContactName = emergencyContactName; this.emergencyContactPhone = emergencyContactPhone;
        this.address = address; this.city = city; this.state = state; this.postalCode = postalCode;
        this.costCenter = costCenter; this.companyName = companyName; this.notes = notes;
        this.hourlyRate = hourlyRate; this.admissionDate = admissionDate; this.terminationDate = terminationDate;
    }

    public String getEmployeeCode() { return employeeCode; }
    public String getName() { return name; }
    public String getDocument() { return document; }
    public String getRole() { return role; }
    public String getSpecialty() { return specialty; }
    public String getType() { return type; }
    public String getEmploymentStatus() { return employmentStatus; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getMobilePhone() { return mobilePhone; }
    public String getEmergencyContactName() { return emergencyContactName; }
    public String getEmergencyContactPhone() { return emergencyContactPhone; }
    public String getAddress() { return address; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getPostalCode() { return postalCode; }
    public String getCostCenter() { return costCenter; }
    public String getCompanyName() { return companyName; }
    public String getNotes() { return notes; }
    public BigDecimal getHourlyRate() { return hourlyRate; }
    public LocalDate getAdmissionDate() { return admissionDate; }
    public LocalDate getTerminationDate() { return terminationDate; }
    public boolean isActive() { return active; }
    public LocalDate getBirthDate() { return birthDate; }
    public String getGender() { return gender; }
    public String getMaritalStatus() { return maritalStatus; }
    public String getRg() { return rg; }
    public String getRgIssuer() { return rgIssuer; }
    public String getPis() { return pis; }
    public String getCtps() { return ctps; }
    public String getCtpsSeries() { return ctpsSeries; }
    public String getBankCode() { return bankCode; }
    public String getBankAgency() { return bankAgency; }
    public String getBankAccountNumber() { return bankAccountNumber; }
    public BigDecimal getSalary() { return salary; }
    public String getDepartment() { return department; }
    public java.util.UUID getCostCenterId() { return costCenterId; }
    public LocalDate getDismissalDate() { return dismissalDate; }
    public String getDismissalReason() { return dismissalReason; }
    public void deactivate() { this.active = false; }

    public void update(String name, String document, String role, String specialty, String type,
                       String employmentStatus, String email, String phone, String mobilePhone,
                       String emergencyContactName, String emergencyContactPhone, String address,
                       String city, String state, String postalCode, String costCenter,
                       String companyName, String notes, BigDecimal hourlyRate,
                       LocalDate admissionDate, LocalDate terminationDate) {
        this.name = name;
        this.document = document;
        this.role = role;
        this.specialty = specialty;
        this.type = type;
        this.employmentStatus = employmentStatus;
        this.email = email;
        this.phone = phone;
        this.mobilePhone = mobilePhone;
        this.emergencyContactName = emergencyContactName;
        this.emergencyContactPhone = emergencyContactPhone;
        this.address = address;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.costCenter = costCenter;
        this.companyName = companyName;
        this.notes = notes;
        this.hourlyRate = hourlyRate;
        this.admissionDate = admissionDate;
        this.terminationDate = terminationDate;
        this.active = "INACTIVE".equalsIgnoreCase(employmentStatus) ? false : this.active;
    }
}
