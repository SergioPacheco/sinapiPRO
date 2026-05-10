package com.sinapipro.api.procurement.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface PurchaseOrderCostDistributionRepository extends JpaRepository<PurchaseOrderCostDistribution, UUID> {
    List<PurchaseOrderCostDistribution> findByPurchaseOrderId(UUID purchaseOrderId);
}
