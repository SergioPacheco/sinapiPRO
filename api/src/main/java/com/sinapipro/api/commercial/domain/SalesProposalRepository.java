package com.sinapipro.api.commercial.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface SalesProposalRepository extends JpaRepository<SalesProposal, UUID> {
    Page<SalesProposal> findByUnitDevelopmentId(UUID developmentId, Pageable pageable);
}
