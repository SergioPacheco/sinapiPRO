package com.sinapipro.api.supplier.domain;

import com.sinapipro.api.shared.domain.SupplierDocumentType;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "supplier_document")
public class SupplierDocument {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(name = "supplier_id", nullable = false) private UUID supplierId;
    @Enumerated(EnumType.STRING) @Column(name = "document_type", nullable = false, length = 30) private SupplierDocumentType documentType;
    @Column(length = 60) private String number;
    @Column(name = "issue_date") private LocalDate issueDate;
    @Column(name = "expiry_date") private LocalDate expiryDate;
    @Column(name = "file_path", length = 500) private String filePath;
    @Column(length = 500) private String notes;
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt = Instant.now();
    @Column(name = "updated_at", nullable = false) private Instant updatedAt = Instant.now();

    protected SupplierDocument() {}
    public SupplierDocument(UUID supplierId, SupplierDocumentType documentType, String number, LocalDate issueDate, LocalDate expiryDate, String filePath, String notes) {
        this.supplierId = supplierId; this.documentType = documentType; this.number = number; this.issueDate = issueDate; this.expiryDate = expiryDate; this.filePath = filePath; this.notes = notes;
    }

    public UUID getId() { return id; }
    public UUID getSupplierId() { return supplierId; }
    public SupplierDocumentType getDocumentType() { return documentType; }
    public String getNumber() { return number; }
    public LocalDate getIssueDate() { return issueDate; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public String getFilePath() { return filePath; }
    public String getNotes() { return notes; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public void update(SupplierDocumentType documentType, String number, LocalDate issueDate, LocalDate expiryDate, String filePath, String notes) {
        this.documentType = documentType; this.number = number; this.issueDate = issueDate; this.expiryDate = expiryDate; this.filePath = filePath; this.notes = notes; this.updatedAt = Instant.now();
    }
}
