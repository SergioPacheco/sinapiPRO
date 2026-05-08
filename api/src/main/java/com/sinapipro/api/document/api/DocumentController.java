package com.sinapipro.api.document.api;

import com.sinapipro.api.document.application.DocumentService;
import com.sinapipro.api.document.domain.Document;
import com.sinapipro.api.shared.api.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Tag(name = "Documents", description = "Document management with versioning")
@RestController
@RequestMapping("/api/v1/budgets/{budgetId}/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @Operation(summary = "List documents for a budget")
    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    PageResponse<DocumentResponse> list(@PathVariable UUID budgetId, @PageableDefault(size = 20) Pageable pageable) {
        return PageResponse.from(documentService.listByBudget(budgetId, pageable).map(DocumentResponse::from));
    }

    @Operation(summary = "Upload a document")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @ResponseStatus(HttpStatus.CREATED)
    DocumentResponse upload(@PathVariable UUID budgetId,
                            @RequestParam("file") MultipartFile file,
                            @RequestParam String title,
                            @RequestParam(required = false) String entityType,
                            @RequestParam(required = false) UUID entityId,
                            @RequestParam(required = false) String uploadedBy) throws IOException {
        return DocumentResponse.from(documentService.upload(budgetId, entityType, entityId, title, file, uploadedBy));
    }

    @Operation(summary = "List document versions for an entity")
    @GetMapping("/versions")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    List<DocumentResponse> versions(@PathVariable UUID budgetId,
                                    @RequestParam String entityType, @RequestParam UUID entityId) {
        return documentService.listVersions(entityType, entityId).stream().map(DocumentResponse::from).toList();
    }

    record DocumentResponse(UUID id, String title, String fileName, String contentType,
                            Long fileSize, Integer version, String uploadedBy, String entityType) {
        static DocumentResponse from(Document d) {
            return new DocumentResponse(d.getId(), d.getTitle(), d.getFileName(), d.getContentType(),
                    d.getFileSize(), d.getVersion(), d.getUploadedBy(), d.getEntityType());
        }
    }
}
