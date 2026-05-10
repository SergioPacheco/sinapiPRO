package com.sinapipro.api.budget.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface BudgetRepository extends JpaRepository<Budget, UUID>, JpaSpecificationExecutor<Budget> {

    Optional<Budget> findByCode(String code);

    boolean existsByCode(String code);

    @Query("""
            SELECT b FROM Budget b
            WHERE (:status IS NULL OR b.status = :status)
              AND (:customerName IS NULL OR LOWER(CAST(b.customerName AS String)) LIKE LOWER(CONCAT('%', CAST(:customerName AS String), '%')))
            ORDER BY b.createdAt DESC
            """)
    Page<Budget> findFiltered(BudgetStatus status, String customerName, Pageable pageable);
}
