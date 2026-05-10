package com.sinapipro.api.supplier.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface SupplierRepository extends JpaRepository<Supplier, UUID> {

    Optional<Supplier> findByCode(String code);

    boolean existsByCode(String code);

    boolean existsByTaxId(String taxId);

    @Query("""
            SELECT s FROM Supplier s
            WHERE (:active IS NULL OR s.active = :active)
              AND (:name IS NULL OR LOWER(CAST(s.name AS String)) LIKE LOWER(CONCAT('%', CAST(:name AS String), '%')))
            ORDER BY s.name ASC
            """)
    Page<Supplier> findFiltered(Boolean active, String name, Pageable pageable);
}
