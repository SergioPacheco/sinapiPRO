package com.sinapipro.api.finance.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ChartOfAccountsRepository extends JpaRepository<ChartOfAccounts, UUID> {
    List<ChartOfAccounts> findByParentIdIsNullAndActiveTrue();
    List<ChartOfAccounts> findByParentIdAndActiveTrue(UUID parentId);
    List<ChartOfAccounts> findByActiveTrue();
}
