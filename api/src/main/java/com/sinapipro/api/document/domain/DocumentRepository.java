package com.sinapipro.api.document.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface DocumentRepository extends JpaRepository<Document, UUID> {
    Page<Document> findByBudgetId(UUID budgetId, Pageable pageable);
    List<Document> findByEntityTypeAndEntityIdOrderByVersionDesc(String entityType, UUID entityId);
    int countByEntityTypeAndEntityIdAndFileName(String entityType, UUID entityId, String fileName);
}
