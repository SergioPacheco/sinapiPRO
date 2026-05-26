package com.sinapipro.api.aftersales.domain;

import com.sinapipro.api.shared.domain.TenantAwareEntity;
import jakarta.persistence.*;
import java.util.UUID;

@Entity @Table(name = "service_ticket_attachment")
public class ServiceTicketAttachment extends TenantAwareEntity {
    @Column(name = "ticket_id", nullable = false) private UUID ticketId;
    @Column(name = "file_name", nullable = false, length = 200) private String fileName;
    @Column(name = "file_path", nullable = false, length = 500) private String filePath;
    @Column(name = "content_type", length = 100) private String contentType;
    @Column(name = "file_size") private Long fileSize;
    @Column(name = "uploaded_by", length = 140) private String uploadedBy;

    protected ServiceTicketAttachment() {}
    public ServiceTicketAttachment(UUID ticketId, String fileName, String filePath, String contentType, Long fileSize, String uploadedBy) {
        this.ticketId = ticketId; this.fileName = fileName; this.filePath = filePath;
        this.contentType = contentType; this.fileSize = fileSize; this.uploadedBy = uploadedBy;
    }

    public UUID getTicketId() { return ticketId; }
    public String getFileName() { return fileName; }
    public String getFilePath() { return filePath; }
    public String getContentType() { return contentType; }
    public Long getFileSize() { return fileSize; }
    public String getUploadedBy() { return uploadedBy; }
}
