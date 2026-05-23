package com.sinapipro.api.registry.domain;

import com.sinapipro.api.shared.domain.RegulatoryStandard;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "employee_training")
public class EmployeeTraining {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(name = "employee_id", nullable = false) private UUID employeeId;
    @Column(name = "training_name", nullable = false, length = 200) private String trainingName;
    @Enumerated(EnumType.STRING) @Column(name = "regulatory_standard", length = 20) private RegulatoryStandard regulatoryStandard;
    @Column(name = "completion_date", nullable = false) private LocalDate completionDate;
    @Column(name = "expiry_date") private LocalDate expiryDate;
    @Column private Integer hours;
    @Column(length = 200) private String institution;
    @Column(name = "certificate_path", length = 500) private String certificatePath;
    @Column(length = 500) private String notes;
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt = Instant.now();
    @Column(name = "updated_at", nullable = false) private Instant updatedAt = Instant.now();

    protected EmployeeTraining() {}
    public EmployeeTraining(UUID employeeId, String trainingName, RegulatoryStandard regulatoryStandard, LocalDate completionDate, LocalDate expiryDate, Integer hours, String institution, String certificatePath, String notes) {
        this.employeeId = employeeId; this.trainingName = trainingName; this.regulatoryStandard = regulatoryStandard; this.completionDate = completionDate; this.expiryDate = expiryDate; this.hours = hours; this.institution = institution; this.certificatePath = certificatePath; this.notes = notes;
    }

    public UUID getId() { return id; }
    public UUID getEmployeeId() { return employeeId; }
    public String getTrainingName() { return trainingName; }
    public RegulatoryStandard getRegulatoryStandard() { return regulatoryStandard; }
    public LocalDate getCompletionDate() { return completionDate; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public Integer getHours() { return hours; }
    public String getInstitution() { return institution; }
    public String getCertificatePath() { return certificatePath; }
    public String getNotes() { return notes; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public void update(String trainingName, RegulatoryStandard regulatoryStandard, LocalDate completionDate, LocalDate expiryDate, Integer hours, String institution, String certificatePath, String notes) {
        this.trainingName = trainingName; this.regulatoryStandard = regulatoryStandard; this.completionDate = completionDate; this.expiryDate = expiryDate; this.hours = hours; this.institution = institution; this.certificatePath = certificatePath; this.notes = notes; this.updatedAt = Instant.now();
    }
}
