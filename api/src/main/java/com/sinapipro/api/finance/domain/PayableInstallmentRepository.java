package com.sinapipro.api.finance.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface PayableInstallmentRepository extends JpaRepository<PayableInstallment, UUID> {
    List<PayableInstallment> findByPayableIdOrderByInstallmentNumber(UUID payableId);
    List<PayableInstallment> findByStatusAndDueDateBefore(InstallmentStatus status, LocalDate date);
}
