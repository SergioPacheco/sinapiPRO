package com.sinapipro.api.commercial.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface BrokerCommissionRepository extends JpaRepository<BrokerCommission, UUID> {
    List<BrokerCommission> findByProposalId(UUID proposalId);
    List<BrokerCommission> findByStatus(String status);
}
