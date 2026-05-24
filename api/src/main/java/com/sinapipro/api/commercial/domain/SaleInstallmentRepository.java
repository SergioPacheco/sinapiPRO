package com.sinapipro.api.commercial.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface SaleInstallmentRepository extends JpaRepository<SaleInstallment, UUID> {
    List<SaleInstallment> findByContractIdOrderByInstallmentNumber(UUID contractId);
    List<SaleInstallment> findByContractIdAndStatus(UUID contractId, String status);
    List<SaleInstallment> findByStatusAndCurrentDueDateBefore(String status, LocalDate date);
}
