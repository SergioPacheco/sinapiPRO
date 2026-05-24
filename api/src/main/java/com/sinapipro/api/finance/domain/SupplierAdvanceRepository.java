package com.sinapipro.api.finance.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface SupplierAdvanceRepository extends JpaRepository<SupplierAdvance, UUID> {
    List<SupplierAdvance> findBySupplierIdAndStatus(UUID supplierId, String status);
}
