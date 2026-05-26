package com.sinapipro.api.finance.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface CheckIssuanceRepository extends JpaRepository<CheckIssuance, UUID> {
    List<CheckIssuance> findByBankAccountIdAndStatus(UUID bankAccountId, String status);
    List<CheckIssuance> findByStatus(String status);
}
