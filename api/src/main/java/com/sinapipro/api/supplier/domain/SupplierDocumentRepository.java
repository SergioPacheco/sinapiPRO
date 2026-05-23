package com.sinapipro.api.supplier.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface SupplierDocumentRepository extends JpaRepository<SupplierDocument, UUID> {
    Page<SupplierDocument> findBySupplierIdOrderByCreatedAtDesc(UUID supplierId, Pageable pageable);

    @Query("SELECT d FROM SupplierDocument d WHERE d.expiryDate IS NOT NULL AND d.expiryDate BETWEEN :from AND :to")
    List<SupplierDocument> findExpiring(LocalDate from, LocalDate to);

    long countBySupplierIdAndExpiryDateBetween(UUID supplierId, LocalDate from, LocalDate to);
}
