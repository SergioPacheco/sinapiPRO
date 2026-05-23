package com.sinapipro.api.registry.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface EmployeeEpiDeliveryRepository extends JpaRepository<EmployeeEpiDelivery, UUID> {
    Page<EmployeeEpiDelivery> findByEmployeeIdOrderByCreatedAtDesc(UUID employeeId, Pageable pageable);

    @Query("SELECT e FROM EmployeeEpiDelivery e WHERE e.expiryDate IS NOT NULL AND e.expiryDate BETWEEN :from AND :to")
    List<EmployeeEpiDelivery> findExpiring(LocalDate from, LocalDate to);

    long countByEmployeeIdAndExpiryDateBetween(UUID employeeId, LocalDate from, LocalDate to);
}
