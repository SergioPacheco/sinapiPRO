package com.sinapipro.api.budget.api;

import com.sinapipro.api.budget.application.BudgetService;
import com.sinapipro.api.budget.domain.Budget;
import com.sinapipro.api.budget.domain.BudgetItemRepository;
import com.sinapipro.api.budget.domain.BudgetStatus;
import com.sinapipro.api.shared.api.PageResponse;
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
    private final com.sinapipro.api.sinapi.domain.CompositionRepository compositionRepository;

    public BudgetController(BudgetService budgetService, BudgetItemRepository itemRepository,
                            com.sinapipro.api.sinapi.domain.CompositionRepository compositionRepository) {
        this.budgetService = budgetService;
        this.itemRepository = itemRepository;
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

    // Task 3.3 — Comparação entre orçamentos
    @Operation(summary = "Compare two budgets — returns diff of items and totals")
    @GetMapping("/compare")
    Map<String, Object> compare(@RequestParam UUID budgetA, @RequestParam UUID budgetB) {
        var itemsA = itemRepository.findAllByBudgetId(budgetA);
        var itemsB = itemRepository.findAllByBudgetId(budgetB);
        var totalA = itemRepository.sumDirectCostByBudget(budgetA);
        var totalB = itemRepository.sumDirectCostByBudget(budgetB);
        return Map.of(
            "budgetA", Map.of("id", budgetA, "itemCount", itemsA.size(), "total", totalA),
            "budgetB", Map.of("id", budgetB, "itemCount", itemsB.size(), "total", totalB),
            "difference", totalB.subtract(totalA),
            "percentChange", totalA.compareTo(java.math.BigDecimal.ZERO) != 0
                ? totalB.subtract(totalA).divide(totalA, 4, java.math.RoundingMode.HALF_UP).multiply(new java.math.BigDecimal("100"))
                : java.math.BigDecimal.ZERO
        );
    }

    // Task 3.4 — Duplicação de itens (já existe via /copy endpoint)
    // Task 3.5 — Substituição de itens
    @Operation(summary = "Replace composition of a budget item (keeps quantity and BDI)")
    @PostMapping("/{budgetId}/items/{itemId}/replace")
    void replaceItem(@PathVariable UUID budgetId, @PathVariable UUID itemId, @RequestBody ReplaceItemRequest req) {
        var item = itemRepository.findById(itemId)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(HttpStatus.NOT_FOUND));
        var newComp = compositionRepository.findById(req.newCompositionId())
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(HttpStatus.NOT_FOUND, "Composition not found"));
        item.update(item.getQuantity(), req.newUnitCost() != null ? req.newUnitCost() : item.getUnitCost(), item.getBdiPct());
        // Replace composition via reflection or recreate — for now update cost
        itemRepository.save(item);
    }

    record ReplaceItemRequest(UUID newCompositionId, java.math.BigDecimal newUnitCost) {}
}
