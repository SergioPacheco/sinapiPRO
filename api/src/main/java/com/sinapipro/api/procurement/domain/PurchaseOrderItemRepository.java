package com.sinapipro.api.procurement.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface PurchaseOrderItemRepository extends JpaRepository<PurchaseOrderItem, UUID> {
    List<PurchaseOrderItem> findByOrderId(UUID orderId);
}
