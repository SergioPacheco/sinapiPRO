package com.sinapipro.api.document.application;

import module java.base;

import com.sinapipro.api.document.domain.Document;
import com.sinapipro.api.document.domain.DocumentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class DocumentService {

    private final DocumentRepository repository;
    private final Path storageRoot;

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "application/pdf", "image/png", "image/jpeg", "image/gif",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-excel", "text/csv", "application/dwg", "application/dxf");

    public DocumentService(DocumentRepository repository,
                           @Value("${sinapipro.storage.path:./uploads}") String storagePath) {
        this.repository = repository;
        this.storageRoot = Path.of(storagePath);
    }

    @Transactional
    public Document upload(UUID budgetId, String entityType, UUID entityId,
                           String title, MultipartFile file, String uploadedBy) throws IOException {
        if (file.getContentType() != null && !ALLOWED_TYPES.contains(file.getContentType())) {
            throw new IllegalArgumentException("File type not allowed: " + file.getContentType());
        }
        var version = repository.countByEntityTypeAndEntityIdAndFileName(entityType, entityId, file.getOriginalFilename()) + 1;

        var relativePath = budgetId + "/" + entityType + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
        var fullPath = storageRoot.resolve(relativePath);
        Files.createDirectories(fullPath.getParent());
        Files.write(fullPath, file.getBytes());

        var doc = new Document(budgetId, entityType, entityId, title,
                file.getOriginalFilename(), file.getContentType(), file.getSize(),
                relativePath, version, uploadedBy);
        return repository.save(doc);
    }

    public Page<Document> listByBudget(UUID budgetId, Pageable pageable) {
        return repository.findByBudgetId(budgetId, pageable);
    }

    public List<Document> listVersions(String entityType, UUID entityId) {
        return repository.findByEntityTypeAndEntityIdOrderByVersionDesc(entityType, entityId);
    }
}
