package com.sinapipro.api.finance.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface CheckReceivedRepository extends JpaRepository<CheckReceived, UUID> {
    List<CheckReceived> findByStatus(String status);
    List<CheckReceived> findByCustodyBankAccountId(UUID bankAccountId);
}
