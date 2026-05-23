package com.sinapipro.api.finance.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface PayableRepository extends JpaRepository<Payable, UUID>, JpaSpecificationExecutor<Payable> {
    Page<Payable> findByBudgetId(UUID budgetId, Pageable pageable);

    List<Payable> findByBudgetIdAndStatus(UUID budgetId, PaymentStatus status);

    @Query("SELECT p FROM Payable p WHERE p.budgetId = :budgetId AND p.status = 'PENDING' AND p.dueDate < :today")
    List<Payable> findOverdue(UUID budgetId, LocalDate today);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payable p WHERE p.budgetId = :budgetId AND p.status = 'PENDING'")
    java.math.BigDecimal sumPendingByBudget(UUID budgetId);

    @Query("SELECT COALESCE(SUM(p.paidAmount), 0) FROM Payable p WHERE p.budgetId = :budgetId AND p.status = 'PAID'")
    java.math.BigDecimal sumPaidByBudget(UUID budgetId);

    @Query("SELECT p FROM Payable p WHERE p.budgetId = :budgetId AND p.dueDate BETWEEN :start AND :end ORDER BY p.dueDate")
    List<Payable> findByBudgetIdAndDueDateBetween(UUID budgetId, LocalDate start, LocalDate end);
}
