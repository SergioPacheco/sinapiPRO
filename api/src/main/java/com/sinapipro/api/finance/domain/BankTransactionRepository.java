package com.sinapipro.api.finance.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface BankTransactionRepository extends JpaRepository<BankTransaction, UUID> {
    Page<BankTransaction> findByBankAccountIdOrderByTransactionDateDesc(UUID bankAccountId, Pageable pageable);
    List<BankTransaction> findByBankAccountIdAndReconciledFalse(UUID bankAccountId);
    List<BankTransaction> findByBankAccountIdAndTransactionDateBetween(UUID bankAccountId, LocalDate from, LocalDate to);
}
