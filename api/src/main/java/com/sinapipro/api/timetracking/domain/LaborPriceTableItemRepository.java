package com.sinapipro.api.timetracking.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface LaborPriceTableItemRepository extends JpaRepository<LaborPriceTableItem, UUID> {
    List<LaborPriceTableItem> findByTableId(UUID tableId);
}
