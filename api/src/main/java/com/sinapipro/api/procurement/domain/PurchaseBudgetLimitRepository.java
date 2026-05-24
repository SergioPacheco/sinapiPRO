package com.sinapipro.api.procurement.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface PurchaseBudgetLimitRepository extends JpaRepository<PurchaseBudgetLimit, UUID> {
    @Query("SELECT l FROM PurchaseBudgetLimit l WHERE l.projectId = :projectId AND l.periodStart <= :date AND l.periodEnd >= :date")
    Optional<PurchaseBudgetLimit> findActiveForProject(UUID projectId, LocalDate date);
}
