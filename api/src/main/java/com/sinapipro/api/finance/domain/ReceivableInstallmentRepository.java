package com.sinapipro.api.finance.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ReceivableInstallmentRepository extends JpaRepository<ReceivableInstallment, UUID> {
    List<ReceivableInstallment> findByReceivableIdOrderByInstallmentNumber(UUID receivableId);
    List<ReceivableInstallment> findByStatusAndDueDateBefore(InstallmentStatus status, LocalDate date);
    List<ReceivableInstallment> findByRemittanceFileIsNullAndStatusAndBankAccountIdIsNotNull(InstallmentStatus status);
}
