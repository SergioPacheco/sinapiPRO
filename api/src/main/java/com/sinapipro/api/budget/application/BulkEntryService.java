package com.sinapipro.api.budget.application;

import com.sinapipro.api.budget.domain.*;
import com.sinapipro.api.shared.error.DomainNotFoundException;
import com.sinapipro.api.sinapi.domain.Composition;
import com.sinapipro.api.sinapi.domain.CompositionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class BulkEntryService {

    private final BudgetRepository budgetRepository;
    private final BudgetStageRepository stageRepository;
    private final BudgetItemRepository itemRepository;
    private final CompositionRepository compositionRepository;

    public BulkEntryService(BudgetRepository budgetRepository, BudgetStageRepository stageRepository,
                            BudgetItemRepository itemRepository, CompositionRepository compositionRepository) {
        this.budgetRepository = budgetRepository;
        this.stageRepository = stageRepository;
        this.itemRepository = itemRepository;
        this.compositionRepository = compositionRepository;
    }

    /**
     * Insere múltiplos itens de uma vez em uma etapa do orçamento.
     */
    public List<BudgetItem> bulkInsert(UUID budgetId, UUID stageId, List<BulkItemEntry> entries) {
        var budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new DomainNotFoundException("Budget not found: " + budgetId));

        if (budget.getStatus() == BudgetStatus.IN_EXECUTION) {
            throw new IllegalStateException("Cannot add items to an effectuated budget");
        }

        var stage = stageRepository.findById(stageId)
                .orElseThrow(() -> new DomainNotFoundException("Stage not found: " + stageId));

        var items = entries.stream().map(entry -> {
            var composition = compositionRepository.findById(entry.compositionId())
                    .orElseThrow(() -> new DomainNotFoundException("Composition not found: " + entry.compositionId()));
            return new BudgetItem(stage, composition, entry.quantity(), entry.unitCost(), entry.bdiPct());
        }).toList();

        return itemRepository.saveAll(items);
    }

    public record BulkItemEntry(UUID compositionId, BigDecimal quantity, BigDecimal unitCost, BigDecimal bdiPct) {}
}
