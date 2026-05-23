package com.sinapipro.api.supplier.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.UUID;

public interface SupplierEvaluationRepository extends JpaRepository<SupplierEvaluation, UUID> {
    Page<SupplierEvaluation> findBySupplierIdOrderByCreatedAtDesc(UUID supplierId, Pageable pageable);

    @Query("SELECT COALESCE(AVG(e.score), 0) FROM SupplierEvaluation e WHERE e.supplierId = :supplierId")
    double averageScoreBySupplierId(UUID supplierId);
}
