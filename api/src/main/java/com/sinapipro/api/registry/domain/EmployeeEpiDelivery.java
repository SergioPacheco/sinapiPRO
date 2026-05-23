package com.sinapipro.api.registry.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "employee_epi_delivery")
public class EmployeeEpiDelivery {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(name = "employee_id", nullable = false) private UUID employeeId;
    @Column(name = "epi_description", nullable = false, length = 200) private String epiDescription;
    @Column(name = "ca_number", length = 30) private String caNumber;
    @Column(name = "delivery_date", nullable = false) private LocalDate deliveryDate;
    @Column(name = "expiry_date") private LocalDate expiryDate;
    @Column(nullable = false) private int quantity = 1;
    @Column(name = "signature_path", length = 500) private String signaturePath;
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt = Instant.now();
    @Column(name = "updated_at", nullable = false) private Instant updatedAt = Instant.now();

    protected EmployeeEpiDelivery() {}
    public EmployeeEpiDelivery(UUID employeeId, String epiDescription, String caNumber, LocalDate deliveryDate, LocalDate expiryDate, int quantity, String signaturePath) {
        this.employeeId = employeeId; this.epiDescription = epiDescription; this.caNumber = caNumber; this.deliveryDate = deliveryDate; this.expiryDate = expiryDate; this.quantity = quantity; this.signaturePath = signaturePath;
    }

    public UUID getId() { return id; }
    public UUID getEmployeeId() { return employeeId; }
    public String getEpiDescription() { return epiDescription; }
    public String getCaNumber() { return caNumber; }
    public LocalDate getDeliveryDate() { return deliveryDate; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public int getQuantity() { return quantity; }
    public String getSignaturePath() { return signaturePath; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public void update(String epiDescription, String caNumber, LocalDate deliveryDate, LocalDate expiryDate, int quantity, String signaturePath) {
        this.epiDescription = epiDescription; this.caNumber = caNumber; this.deliveryDate = deliveryDate; this.expiryDate = expiryDate; this.quantity = quantity; this.signaturePath = signaturePath; this.updatedAt = Instant.now();
    }
}
