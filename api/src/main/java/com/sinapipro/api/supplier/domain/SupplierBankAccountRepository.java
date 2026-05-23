package com.sinapipro.api.supplier.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface SupplierBankAccountRepository extends JpaRepository<SupplierBankAccount, UUID> {
    Page<SupplierBankAccount> findBySupplierIdOrderByCreatedAtDesc(UUID supplierId, Pageable pageable);
}
