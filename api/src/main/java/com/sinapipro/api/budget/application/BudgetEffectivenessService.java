package com.sinapipro.api.budget.application;

import com.sinapipro.api.budget.domain.*;
import com.sinapipro.api.shared.error.DomainNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class BudgetEffectivenessService {

    private final BudgetRepository budgetRepository;
    private final BudgetItemRepository itemRepository;

    public BudgetEffectivenessService(BudgetRepository budgetRepository,
                                       BudgetItemRepository itemRepository) {
        this.budgetRepository = budgetRepository;
        this.itemRepository = itemRepository;
    }

    /**
     * Efetiva um orçamento: DRAFT/APPROVED → IN_EXECUTION.
     * Após efetivação, composições e quantidades não podem ser alteradas.
     */
    public Budget effectuate(UUID budgetId) {
        var budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new DomainNotFoundException("Budget not found: " + budgetId));

        if (budget.getStatus() != BudgetStatus.DRAFT && budget.getStatus() != BudgetStatus.APPROVED) {
            throw new IllegalStateException(
                    "Only DRAFT or APPROVED budgets can be effectuated. Current: " + budget.getStatus());
        }

        var items = itemRepository.findAllByBudgetId(budgetId);
        if (items.isEmpty()) {
            throw new IllegalStateException("Cannot effectuate a budget with no items");
        }

        budget.setStatus(BudgetStatus.IN_EXECUTION);
        budget.setActive(true);
        return budgetRepository.save(budget);
    }

    /**
     * Reverte efetivação (volta para APPROVED). Só possível se não houver medições vinculadas.
     */
    public Budget revert(UUID budgetId) {
        var budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new DomainNotFoundException("Budget not found: " + budgetId));

        if (budget.getStatus() != BudgetStatus.IN_EXECUTION) {
            throw new IllegalStateException("Only IN_EXECUTION budgets can be reverted");
        }

        budget.setStatus(BudgetStatus.APPROVED);
        return budgetRepository.save(budget);
    }
}
