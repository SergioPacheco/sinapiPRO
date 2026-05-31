package com.sinapipro.api.jobcosting.api;

import com.sinapipro.api.budget.domain.Budget;
import com.sinapipro.api.budget.domain.BudgetRepository;
import com.sinapipro.api.jobcosting.application.JobCostingService;
import com.sinapipro.api.jobcosting.application.WipReportService;
import com.sinapipro.api.jobcosting.domain.*;
import com.sinapipro.api.shared.error.DomainNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Tag(name = "Job Costing", description = "Cost codes and cost tracking (budgeted vs actual vs committed)")
@RestController
@RequestMapping("/api/v1/projects/{projectId}/cost-codes")
public class JobCostingController {

    private final CostCodeRepository codeRepository;
    private final CostTransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;
    private final JobCostingService jobCostingService;
    private final WipReportService wipReportService;

    public JobCostingController(CostCodeRepository codeRepository, CostTransactionRepository transactionRepository,
                                BudgetRepository budgetRepository, JobCostingService jobCostingService,
                                WipReportService wipReportService) {
        this.codeRepository = codeRepository;
        this.transactionRepository = transactionRepository;
        this.budgetRepository = budgetRepository;
        this.jobCostingService = jobCostingService;
        this.wipReportService = wipReportService;
    }

    @Operation(summary = "List all cost codes for a budget")
    @GetMapping
    @PreAuthorize("@perm.check('budget.read')")
    List<CostCodeResponse> list(@PathVariable UUID projectId) {
        return codeRepository.findByBudgetIdOrderByCode(projectId).stream().map(CostCodeResponse::from).toList();
    }

    @Operation(summary = "Create a cost code")
    @PostMapping
    @PreAuthorize("@perm.check('budget.write')")
    ResponseEntity<CostCodeResponse> create(@PathVariable UUID projectId, @Valid @RequestBody CreateCostCodeRequest req) {
        Budget budget = budgetRepository.findById(projectId)
                .orElseThrow(() -> new DomainNotFoundException("Budget not found: " + projectId));
        CostCode parent = req.parentId() != null ? codeRepository.findById(req.parentId()).orElse(null) : null;
        CostCode saved = codeRepository.save(new CostCode(budget, parent, req.code(), req.name(), req.budgetedAmount()));
        return ResponseEntity.created(URI.create("/api/v1/budgets/" + projectId + "/cost-codes/" + saved.getId()))
                .body(CostCodeResponse.from(saved));
    }

    @Operation(summary = "Delete a cost code")
    @DeleteMapping("/{codeId}")
    @PreAuthorize("@perm.check('budget.write')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable UUID projectId, @PathVariable UUID codeId) {
        codeRepository.deleteById(codeId);
    }

    @Operation(summary = "Record a cost transaction (actual or committed)")
    @PostMapping("/{codeId}/transactions")
    @PreAuthorize("@perm.check('budget.write')")
    @ResponseStatus(HttpStatus.CREATED)
    TransactionResponse recordTransaction(@PathVariable UUID projectId, @PathVariable UUID codeId,
                                          @Valid @RequestBody CreateTransactionRequest req) {
        CostCode code = codeRepository.findById(codeId)
                .orElseThrow(() -> new DomainNotFoundException("Cost code not found: " + codeId));
        CostTransaction tx = transactionRepository.save(
                new CostTransaction(code, req.type(), req.amount(), req.description(), req.referenceId(), req.transactionDate()));
        return TransactionResponse.from(tx);
    }

    @Operation(summary = "List transactions for a cost code")
    @GetMapping("/{codeId}/transactions")
    @PreAuthorize("@perm.check('budget.read')")
    List<TransactionResponse> listTransactions(@PathVariable UUID projectId, @PathVariable UUID codeId) {
        return transactionRepository.findByCostCodeIdOrderByTransactionDateDesc(codeId).stream()
                .map(TransactionResponse::from).toList();
    }

    @Operation(summary = "Variance summary per cost code")
    @GetMapping("/summary")
    @PreAuthorize("@perm.check('budget.read')")
    List<JobCostingService.CostCodeSummary> summary(@PathVariable UUID projectId) {
        return jobCostingService.summarizeAll(projectId);
    }

    @Operation(summary = "Budget-level cost summary (total budgeted, actual, committed, variance)")
    @GetMapping("/budget-summary")
    @PreAuthorize("@perm.check('budget.read')")
    JobCostingService.BudgetCostSummary budgetSummary(@PathVariable UUID projectId) {
        return jobCostingService.budgetSummary(projectId);
    }

    @Operation(summary = "WIP Report — over/under billing analysis")
    @GetMapping("/wip-report")
    @PreAuthorize("@perm.check('budget.read')")
    WipReportService.WipReport wipReport(@PathVariable UUID projectId) {
        return wipReportService.calculate(projectId);
    }

    // --- DTOs ---
    record CreateCostCodeRequest(@NotBlank String code, @NotBlank String name,
                                 @NotNull BigDecimal budgetedAmount, UUID parentId) {}
    record CreateTransactionRequest(@NotNull CostTransactionType type, @NotNull @Positive BigDecimal amount,
                                    String description, UUID referenceId, @NotNull LocalDate transactionDate) {}

    record CostCodeResponse(UUID id, UUID parentId, String code, String name, BigDecimal budgetedAmount) {
        static CostCodeResponse from(CostCode c) {
            return new CostCodeResponse(c.getId(), c.getParentId(), c.getCode(), c.getName(), c.getBudgetedAmount());
        }
    }

    record TransactionResponse(UUID id, CostTransactionType type, BigDecimal amount, String description,
                               LocalDate transactionDate) {
        static TransactionResponse from(CostTransaction t) {
            return new TransactionResponse(t.getId(), t.getType(), t.getAmount(), t.getDescription(), t.getTransactionDate());
        }
    }
}
