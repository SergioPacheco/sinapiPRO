package com.sinapipro.api.invoice.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {

    boolean existsByNumber(String number);

    @Query("""
            SELECT i FROM Invoice i
            JOIN FETCH i.budget
            JOIN FETCH i.supplier
            WHERE (:status IS NULL OR i.status = :status)
              AND (:budgetId IS NULL OR i.budget.id = :budgetId)
              AND (:supplierId IS NULL OR i.supplier.id = :supplierId)
            """)
    Page<Invoice> findFiltered(InvoiceStatus status, UUID budgetId, UUID supplierId, Pageable pageable);
}
