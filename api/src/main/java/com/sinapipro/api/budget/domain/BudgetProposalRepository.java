package com.sinapipro.api.budget.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BudgetProposalRepository extends JpaRepository<BudgetProposal, UUID> {
    List<BudgetProposal> findByBudgetIdOrderByCreatedAtDesc(UUID budgetId);
}
