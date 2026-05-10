package com.sinapipro.api.registry.domain;

import com.sinapipro.api.shared.domain.AuditableEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "employee")
public class Employee extends AuditableEntity {
    @Column(nullable = false, length = 200) private String name;
    @Column(length = 20) private String document;
    @Column(nullable = false, length = 80) private String role;
    @Column(nullable = false, length = 20) private String type; // EMPLOYEE, CONTRACTOR
    @Column(length = 200) private String email;
    @Column(length = 30) private String phone;
    @Column(name = "hourly_rate", precision = 14, scale = 4) private BigDecimal hourlyRate;
    @Column(name = "admission_date") private LocalDate admissionDate;
    @Column(nullable = false) private boolean active = true;

    protected Employee() {}
    public Employee(String name, String document, String role, String type, String email, String phone, BigDecimal hourlyRate, LocalDate admissionDate) {
        this.name = name; this.document = document; this.role = role; this.type = type;
        this.email = email; this.phone = phone; this.hourlyRate = hourlyRate; this.admissionDate = admissionDate;
    }

    public String getName() { return name; }
    public String getDocument() { return document; }
    public String getRole() { return role; }
    public String getType() { return type; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public BigDecimal getHourlyRate() { return hourlyRate; }
    public LocalDate getAdmissionDate() { return admissionDate; }
    public boolean isActive() { return active; }
    public void deactivate() { this.active = false; }
}
