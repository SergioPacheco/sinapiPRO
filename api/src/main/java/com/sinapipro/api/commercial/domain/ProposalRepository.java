package com.sinapipro.api.commercial.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ProposalRepository extends JpaRepository<Proposal, UUID> {
    Page<Proposal> findByStatus(String status, Pageable pageable);
    Page<Proposal> findByClientId(UUID clientId, Pageable pageable);
}
