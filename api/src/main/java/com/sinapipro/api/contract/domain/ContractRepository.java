package com.sinapipro.api.contract.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ContractRepository extends JpaRepository<Contract, UUID> {
    List<Contract> findByBudgetIdOrderByNumberAsc(UUID budgetId);
    Page<Contract> findByBudgetId(UUID budgetId, Pageable pageable);
}
