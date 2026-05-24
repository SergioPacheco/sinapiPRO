package com.sinapipro.api.document.domain;
import com.sinapipro.api.shared.domain.TenantAwareEntity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "document")
public class Document extends TenantAwareEntity {

    private UUID id;

    @Column(name = "budget_id")
    private UUID budgetId;

    @Column(name = "entity_type", length = 40)
    private String entityType;

    @Column(name = "entity_id")
    private UUID entityId;

    @Column(nullable = false, length = 300)
    private String title;

    @Column(name = "file_name", nullable = false, length = 260)
    private String fileName;

    @Column(name = "content_type", nullable = false, length = 100)
    private String contentType;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Column(name = "storage_path", nullable = false, length = 500)
    private String storagePath;

    @Column(nullable = false)
    private Integer version;

    @Column(name = "uploaded_by", length = 140)
    private String uploadedBy;



    protected Document() {}

    public Document(UUID budgetId, String entityType, UUID entityId, String title,
                    String fileName, String contentType, Long fileSize, String storagePath,
                    Integer version, String uploadedBy) {
        this.budgetId = budgetId;
        this.entityType = entityType;
        this.entityId = entityId;
        this.title = title;
        this.fileName = fileName;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.storagePath = storagePath;
        this.version = version;
        this.uploadedBy = uploadedBy;
    }

    public UUID getBudgetId() { return budgetId; }
    public String getEntityType() { return entityType; }
    public UUID getEntityId() { return entityId; }
    public String getTitle() { return title; }
    public String getFileName() { return fileName; }
    public String getContentType() { return contentType; }
    public Long getFileSize() { return fileSize; }
    public String getStoragePath() { return storagePath; }
    public Integer getVersion() { return version; }
    public String getUploadedBy() { return uploadedBy; }
}
