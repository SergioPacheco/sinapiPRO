package com.sinapipro.api.registry.domain;

import com.sinapipro.api.shared.domain.MedicalExamResult;
import com.sinapipro.api.shared.domain.MedicalExamType;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "employee_medical_exam")
public class EmployeeMedicalExam {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(name = "employee_id", nullable = false) private UUID employeeId;
    @Enumerated(EnumType.STRING) @Column(name = "exam_type", nullable = false, length = 30) private MedicalExamType examType;
    @Column(name = "exam_date", nullable = false) private LocalDate examDate;
    @Column(name = "expiry_date") private LocalDate expiryDate;
    @Column(length = 200) private String physician;
    @Column(length = 20) private String crm;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 30) private MedicalExamResult result = MedicalExamResult.APTO;
    @Column(length = 500) private String notes;
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt = Instant.now();
    @Column(name = "updated_at", nullable = false) private Instant updatedAt = Instant.now();

    protected EmployeeMedicalExam() {}
    public EmployeeMedicalExam(UUID employeeId, MedicalExamType examType, LocalDate examDate, LocalDate expiryDate, String physician, String crm, MedicalExamResult result, String notes) {
        this.employeeId = employeeId; this.examType = examType; this.examDate = examDate; this.expiryDate = expiryDate; this.physician = physician; this.crm = crm; this.result = result; this.notes = notes;
    }

    public UUID getId() { return id; }
    public UUID getEmployeeId() { return employeeId; }
    public MedicalExamType getExamType() { return examType; }
    public LocalDate getExamDate() { return examDate; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public String getPhysician() { return physician; }
    public String getCrm() { return crm; }
    public MedicalExamResult getResult() { return result; }
    public String getNotes() { return notes; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public void update(MedicalExamType examType, LocalDate examDate, LocalDate expiryDate, String physician, String crm, MedicalExamResult result, String notes) {
        this.examType = examType; this.examDate = examDate; this.expiryDate = expiryDate; this.physician = physician; this.crm = crm; this.result = result; this.notes = notes; this.updatedAt = Instant.now();
    }
}
