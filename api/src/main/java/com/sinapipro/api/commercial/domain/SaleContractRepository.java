package com.sinapipro.api.commercial.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface SaleContractRepository extends JpaRepository<SaleContract, UUID> {
    Page<SaleContract> findByDevelopmentId(UUID developmentId, Pageable pageable);
    List<SaleContract> findByDevelopmentIdAndStatus(UUID developmentId, String status);
}
