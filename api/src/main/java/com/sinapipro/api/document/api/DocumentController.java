package com.sinapipro.api.document.api;

import com.sinapipro.api.document.application.DocumentService;
import com.sinapipro.api.document.domain.Document;
import com.sinapipro.api.document.domain.DocumentVersion;
import com.sinapipro.api.document.domain.DocumentVersionRepository;
import com.sinapipro.api.document.domain.DocumentRepository;
import com.sinapipro.api.shared.api.PageResponse;
import com.sinapipro.api.shared.error.DomainNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Tag(name = "Documents", description = "Document management with versioning")
@RestController
@RequestMapping("/api/v1/projects/{projectId}/documents")
public class DocumentController {

    private final DocumentService documentService;
    private final DocumentRepository documentRepository;
    private final DocumentVersionRepository versionRepository;

    public DocumentController(DocumentService documentService, DocumentRepository documentRepository,
                              DocumentVersionRepository versionRepository) {
        this.documentService = documentService;
        this.documentRepository = documentRepository;
        this.versionRepository = versionRepository;
    }

    @Operation(summary = "List documents for a budget")
    @GetMapping
    @PreAuthorize("@perm.check('budget.read')")
    PageResponse<DocumentResponse> list(@PathVariable UUID projectId, @PageableDefault(size = 20) Pageable pageable) {
        return PageResponse.from(documentService.listByBudget(projectId, pageable).map(DocumentResponse::from));
    }

    @Operation(summary = "Upload a document")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("@perm.check('budget.write')")
    @ResponseStatus(HttpStatus.CREATED)
    DocumentResponse upload(@PathVariable UUID projectId,
                            @RequestParam("file") MultipartFile file,
                            @RequestParam String title,
                            @RequestParam(required = false) String entityType,
                            @RequestParam(required = false) UUID entityId,
                            @RequestParam(required = false) String uploadedBy) throws IOException {
        return DocumentResponse.from(documentService.upload(projectId, entityType, entityId, title, file, uploadedBy));
    }

    @Operation(summary = "List document versions for an entity")
    @GetMapping("/versions")
    @PreAuthorize("@perm.check('budget.read')")
    List<DocumentResponse> versions(@PathVariable UUID projectId,
                                    @RequestParam String entityType, @RequestParam UUID entityId) {
        return documentService.listVersions(entityType, entityId).stream().map(DocumentResponse::from).toList();
    }

    // --- Version History ---

    @Operation(summary = "Add a new version to an existing document")
    @PostMapping("/{documentId}/versions")
    @PreAuthorize("@perm.check('budget.write')")
    @Transactional
    @ResponseStatus(HttpStatus.CREATED)
    VersionResponse addVersion(@PathVariable UUID projectId, @PathVariable UUID documentId,
                               @Valid @RequestBody CreateVersionRequest req) {
        var document = documentRepository.findById(documentId)
                .orElseThrow(() -> new DomainNotFoundException("Document not found: " + documentId));
        var versions = versionRepository.findByDocumentIdOrderByVersionNumberDesc(documentId);
        int nextVersion = versions.isEmpty() ? 2 : versions.getFirst().getVersionNumber() + 1;
        var version = versionRepository.save(new DocumentVersion(document, nextVersion, req.filePath(), req.uploadedBy(), req.notes()));
        return VersionResponse.from(version);
    }

    @Operation(summary = "List version history of a document")
    @GetMapping("/{documentId}/versions")
    @PreAuthorize("@perm.check('budget.read')")
    List<VersionResponse> listVersionHistory(@PathVariable UUID projectId, @PathVariable UUID documentId) {
        return versionRepository.findByDocumentIdOrderByVersionNumberDesc(documentId).stream()
                .map(VersionResponse::from).toList();
    }

    // --- DTOs ---

    record CreateVersionRequest(@NotBlank String filePath, String uploadedBy, String notes) {}

    record DocumentResponse(UUID id, String title, String fileName, String contentType,
                            Long fileSize, Integer version, String uploadedBy, String entityType) {
        static DocumentResponse from(Document d) {
            return new DocumentResponse(d.getId(), d.getTitle(), d.getFileName(), d.getContentType(),
                    d.getFileSize(), d.getVersion(), d.getUploadedBy(), d.getEntityType());
        }
    }

    record VersionResponse(UUID id, int versionNumber, String filePath, String uploadedBy, String notes, String createdAt) {
        static VersionResponse from(DocumentVersion v) {
            return new VersionResponse(v.getId(), v.getVersionNumber(), v.getFilePath(), v.getUploadedBy(),
                    v.getNotes(), v.getCreatedAt() != null ? v.getCreatedAt().toString() : null);
        }
    }
}
