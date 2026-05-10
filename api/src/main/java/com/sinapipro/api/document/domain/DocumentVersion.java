package com.sinapipro.api.document.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "document_version", uniqueConstraints = @UniqueConstraint(columnNames = {"document_id", "version_number"}))
public class DocumentVersion {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "document_id", nullable = false) private Document document;
    @Column(name = "version_number", nullable = false) private int versionNumber;
    @Column(name = "file_path", nullable = false, length = 500) private String filePath;
    @Column(name = "uploaded_by", length = 140) private String uploadedBy;
    @Column(length = 300) private String notes;
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt;
    @PrePersist void prePersist() { createdAt = Instant.now(); }

    protected DocumentVersion() {}
    public DocumentVersion(Document document, int versionNumber, String filePath, String uploadedBy, String notes) {
        this.document = document; this.versionNumber = versionNumber; this.filePath = filePath;
        this.uploadedBy = uploadedBy; this.notes = notes;
    }

    public UUID getId() { return id; }
    public Document getDocument() { return document; }
    public int getVersionNumber() { return versionNumber; }
    public String getFilePath() { return filePath; }
    public String getUploadedBy() { return uploadedBy; }
    public String getNotes() { return notes; }
    public Instant getCreatedAt() { return createdAt; }
}
