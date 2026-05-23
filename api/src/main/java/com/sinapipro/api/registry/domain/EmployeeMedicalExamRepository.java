package com.sinapipro.api.registry.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface EmployeeMedicalExamRepository extends JpaRepository<EmployeeMedicalExam, UUID> {
    Page<EmployeeMedicalExam> findByEmployeeIdOrderByCreatedAtDesc(UUID employeeId, Pageable pageable);

    @Query("SELECT e FROM EmployeeMedicalExam e WHERE e.expiryDate IS NOT NULL AND e.expiryDate BETWEEN :from AND :to")
    List<EmployeeMedicalExam> findExpiring(LocalDate from, LocalDate to);

    long countByEmployeeIdAndExpiryDateBetween(UUID employeeId, LocalDate from, LocalDate to);
}
