package com.sinapipro.api.commercial.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface PriceTableRepository extends JpaRepository<PriceTable, UUID> {
    List<PriceTable> findByDevelopmentIdAndActiveTrue(UUID developmentId);
}

interface PriceTableItemRepository extends JpaRepository<PriceTableItem, UUID> {
    List<PriceTableItem> findByPriceTableId(UUID priceTableId);
}

interface SaleContractUnitRepository extends JpaRepository<SaleContractUnit, UUID> {
    List<SaleContractUnit> findByContractId(UUID contractId);
}

interface SaleContractProponentRepository extends JpaRepository<SaleContractProponent, UUID> {
    List<SaleContractProponent> findByContractId(UUID contractId);
}
