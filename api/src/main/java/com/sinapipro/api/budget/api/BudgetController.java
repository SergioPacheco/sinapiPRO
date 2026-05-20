package com.sinapipro.api.budget.api;

import com.sinapipro.api.budget.application.BudgetService;
import com.sinapipro.api.budget.domain.Budget;
import com.sinapipro.api.budget.domain.BudgetItemRepository;
import com.sinapipro.api.budget.domain.BudgetStageRepository;
import com.sinapipro.api.budget.domain.BudgetStatus;
import com.sinapipro.api.shared.api.PageResponse;
import com.sinapipro.api.shared.error.DomainNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

@Tag(name = "Budgets", description = "Construction budget management")
@RestController
@RequestMapping("/api/v1/projects/{projectId}/budgets")
public class BudgetController {

    private final BudgetService budgetService;
    private final BudgetItemRepository itemRepository;
    private final BudgetStageRepository stageRepository;
    private final com.sinapipro.api.sinapi.domain.CompositionRepository compositionRepository;

    public BudgetController(BudgetService budgetService, BudgetItemRepository itemRepository,
                            BudgetStageRepository stageRepository,
                            com.sinapipro.api.sinapi.domain.CompositionRepository compositionRepository) {
        this.budgetService = budgetService;
        this.itemRepository = itemRepository;
        this.stageRepository = stageRepository;
        this.compositionRepository = compositionRepository;
    }

    @Operation(summary = "List budgets with filters and pagination")
    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    PageResponse<BudgetResponse> list(
            @RequestParam(required = false) BudgetStatus status,
            @RequestParam(required = false) String customerName,
            @PageableDefault(size = 20) Pageable pageable) {
        var page = budgetService.findAll(new BudgetFilter(status, customerName), pageable);
        return PageResponse.from(page.map(BudgetResponse::from));
    }

    @Operation(summary = "Get budget by ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    BudgetResponse findById(@PathVariable UUID id) {
        return BudgetResponse.from(budgetService.findById(id));
    }

    @Operation(summary = "Get budget by code")
    @GetMapping("/code/{code}")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    BudgetResponse findByCode(@PathVariable String code) {
        return BudgetResponse.from(budgetService.findByCode(code));
    }

    @Operation(summary = "Create a new budget")
    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    ResponseEntity<BudgetResponse> create(@Valid @RequestBody CreateBudgetRequest request) {
        var budget = budgetService.create(request);
        var response = BudgetResponse.from(budget);
        return ResponseEntity.created(URI.create("/api/v1/budgets/" + budget.getId())).body(response);
    }

    @Operation(summary = "Update an existing budget")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    BudgetResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateBudgetRequest request) {
        return BudgetResponse.from(budgetService.update(id, request));
    }

    @Operation(summary = "Copy a budget with stages, items and BDI")
    @PostMapping("/{id}/copy")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    ResponseEntity<BudgetResponse> copy(@PathVariable UUID id, @Valid @RequestBody CopyBudgetRequest request) {
        var budget = budgetService.copy(id, request.code(), request.title());
        return ResponseEntity.status(HttpStatus.CREATED).body(BudgetResponse.from(budget));
    }

    @Operation(summary = "Activate a budget as the current execution budget")
    @PostMapping("/{id}/activate")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    BudgetResponse activate(@PathVariable UUID id) {
        return BudgetResponse.from(budgetService.activate(id));
    }

    @Operation(summary = "Delete a budget")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable UUID id) {
        budgetService.delete(id);
    }

    record CopyBudgetRequest(
            @jakarta.validation.constraints.NotBlank String code,
            @jakarta.validation.constraints.NotBlank String title) {}

    // Task 3.3 — Comparação entre orçamentos (item-by-item diff)
    @Operation(summary = "Compare two budgets — returns item-by-item diff and totals")
    @GetMapping("/compare")
    CompareResponse compare(@RequestParam UUID budgetA, @RequestParam UUID budgetB) {
        var itemsA = itemRepository.findAllByBudgetId(budgetA);
        var itemsB = itemRepository.findAllByBudgetId(budgetB);
        var totalA = itemRepository.sumDirectCostByBudget(budgetA);
        var totalB = itemRepository.sumDirectCostByBudget(budgetB);

        // Build map by composition ID for diff
        var mapA = itemsA.stream().collect(java.util.stream.Collectors.toMap(
                i -> i.getComposition().getId(), i -> i, (a, b) -> a));
        var mapB = itemsB.stream().collect(java.util.stream.Collectors.toMap(
                i -> i.getComposition().getId(), i -> i, (a, b) -> a));

        var allKeys = new java.util.LinkedHashSet<>(mapA.keySet());
        allKeys.addAll(mapB.keySet());

        var diffs = allKeys.stream().map(key -> {
            var a = mapA.get(key);
            var b = mapB.get(key);
            if (a != null && b == null) return new CompareItem(a.getComposition().getSinapiCode(),
                    a.getComposition().getDescription(), "REMOVED", a.getQuantity(), null, a.getUnitCost(), null);
            if (a == null && b != null) return new CompareItem(b.getComposition().getSinapiCode(),
                    b.getComposition().getDescription(), "ADDED", null, b.getQuantity(), null, b.getUnitCost());
            String status = a.getQuantity().compareTo(b.getQuantity()) == 0 && a.getUnitCost().compareTo(b.getUnitCost()) == 0
                    ? "UNCHANGED" : "MODIFIED";
            return new CompareItem(a.getComposition().getSinapiCode(), a.getComposition().getDescription(),
                    status, a.getQuantity(), b.getQuantity(), a.getUnitCost(), b.getUnitCost());
        }).toList();

        var pctChange = totalA.compareTo(java.math.BigDecimal.ZERO) != 0
                ? totalB.subtract(totalA).divide(totalA, 4, java.math.RoundingMode.HALF_UP).multiply(new java.math.BigDecimal("100"))
                : java.math.BigDecimal.ZERO;

        return new CompareResponse(budgetA, budgetB, totalA, totalB, totalB.subtract(totalA), pctChange, diffs);
    }

    record CompareResponse(UUID budgetA, UUID budgetB, java.math.BigDecimal totalA, java.math.BigDecimal totalB,
                           java.math.BigDecimal difference, java.math.BigDecimal percentChange, java.util.List<CompareItem> items) {}
    record CompareItem(String code, String description, String status,
                       java.math.BigDecimal qtyA, java.math.BigDecimal qtyB,
                       java.math.BigDecimal costA, java.math.BigDecimal costB) {}

    // Task 3.4 — Duplicação de itens (já existe via /copy endpoint)

    // Task 3.5 — Substituição de itens (troca composição de fato)
    @Operation(summary = "Replace composition of a budget item (keeps quantity and BDI, swaps composition)")
    @PostMapping("/{budgetId}/items/{itemId}/replace")
    @org.springframework.transaction.annotation.Transactional
    void replaceItem(@PathVariable UUID budgetId, @PathVariable UUID itemId, @RequestBody ReplaceItemRequest req) {
        var item = itemRepository.findById(itemId)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(HttpStatus.NOT_FOUND));
        var newComp = compositionRepository.findById(req.newCompositionId())
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(HttpStatus.NOT_FOUND, "Composition not found"));
        // Create new item with new composition, preserving quantity and BDI
        var stage = item.getStage();
        java.math.BigDecimal unitCost = req.newUnitCost() != null ? req.newUnitCost() : item.getUnitCost();
        var newItem = new com.sinapipro.api.budget.domain.BudgetItem(stage, newComp, item.getQuantity(), unitCost, item.getBdiPct());
        newItem.setCustomCode(item.getCustomCode());
        itemRepository.save(newItem);
        itemRepository.delete(item);
    }

    // Task 3.6 — Importar itens de outro orçamento
    @Operation(summary = "Import items from another budget into a stage")
    @PostMapping("/{budgetId}/stages/{stageId}/import-items")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @org.springframework.transaction.annotation.Transactional
    ResponseEntity<BulkAddResult> importItems(@PathVariable UUID budgetId, @PathVariable UUID stageId,
                                              @RequestBody ImportItemsRequest req) {
        var stage = ensureStageInBudget(budgetId, stageId);
        var sourceItems = itemRepository.findAllByBudgetId(req.sourceBudgetId());
        int added = 0;
        for (var source : sourceItems) {
            if (req.itemIds() != null && !req.itemIds().contains(source.getId())) continue;
            var newItem = new com.sinapipro.api.budget.domain.BudgetItem(
                    stage, source.getComposition(), source.getQuantity(), source.getUnitCost(), source.getBdiPct());
            newItem.setCustomCode(source.getCustomCode());
            newItem.setPriceSource(source.getPriceSource());
            itemRepository.save(newItem);
            added++;
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(new BulkAddResult(added, 0, added));
    }

    record ImportItemsRequest(@jakarta.validation.constraints.NotNull UUID sourceBudgetId, java.util.List<UUID> itemIds) {}

    record ReplaceItemRequest(UUID newCompositionId, java.math.BigDecimal newUnitCost) {}

    record BulkAddResult(int added, int skipped, int total) {}

    private com.sinapipro.api.budget.domain.BudgetStage ensureStageInBudget(UUID budgetId, UUID stageId) {
        var stage = stageRepository.findById(stageId)
                .orElseThrow(() -> new DomainNotFoundException("Stage not found: " + stageId));
        if (!budgetId.equals(stage.getBudget().getId())) {
            throw new DomainNotFoundException("Stage not found in budget: " + stageId);
        }
        return stage;
    }
}
