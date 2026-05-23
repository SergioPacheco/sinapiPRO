package com.sinapipro.api.finance.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ReceivableRepository extends JpaRepository<Receivable, UUID>, JpaSpecificationExecutor<Receivable> {
    Page<Receivable> findByBudgetId(UUID budgetId, Pageable pageable);

    List<Receivable> findByBudgetIdAndStatus(UUID budgetId, PaymentStatus status);

    @Query("SELECT r FROM Receivable r WHERE r.budgetId = :budgetId AND r.status = 'PENDING' AND r.dueDate < :today")
    List<Receivable> findOverdue(UUID budgetId, LocalDate today);

    @Query("SELECT COALESCE(SUM(r.amount), 0) FROM Receivable r WHERE r.budgetId = :budgetId AND r.status = 'PENDING'")
    java.math.BigDecimal sumPendingByBudget(UUID budgetId);

    @Query("SELECT COALESCE(SUM(r.receivedAmount), 0) FROM Receivable r WHERE r.budgetId = :budgetId AND r.status = 'PAID'")
    java.math.BigDecimal sumReceivedByBudget(UUID budgetId);

    @Query("SELECT r FROM Receivable r WHERE r.budgetId = :budgetId AND r.dueDate BETWEEN :start AND :end ORDER BY r.dueDate")
    List<Receivable> findByBudgetIdAndDueDateBetween(UUID budgetId, LocalDate start, LocalDate end);
}
