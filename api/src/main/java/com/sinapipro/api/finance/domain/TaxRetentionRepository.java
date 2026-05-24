package com.sinapipro.api.finance.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface TaxRetentionRepository extends JpaRepository<TaxRetention, UUID> {
    List<TaxRetention> findByPayableId(UUID payableId);
}
