package com.sinapipro.api.budget.api;

import com.sinapipro.api.budget.application.BudgetService;
import com.sinapipro.api.budget.domain.Budget;
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
import java.util.UUID;

@Tag(name = "Budgets", description = "Construction budget management")
@RestController
@RequestMapping("/api/v1/budgets")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
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

    @Operation(summary = "Delete a budget")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable UUID id) {
        budgetService.delete(id);
    }
}
