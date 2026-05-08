package com.sinapipro.api.contract.api;

import com.sinapipro.api.contract.application.ContractService;
import com.sinapipro.api.contract.application.ContractService.ContractFinancialSummary;
import com.sinapipro.api.contract.domain.*;
import com.sinapipro.api.shared.api.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Tag(name = "Contracts", description = "Contracts with change orders (aditivos)")
@RestController
@RequestMapping("/api/v1/budgets/{budgetId}/contracts")
public class ContractController {

    private final ContractService contractService;
    private final ContractRepository contractRepository;

    public ContractController(ContractService contractService, ContractRepository contractRepository) {
        this.contractService = contractService;
        this.contractRepository = contractRepository;
    }

    @Operation(summary = "List contracts for a budget")
    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    PageResponse<ContractResponse> list(@PathVariable UUID budgetId, @PageableDefault(size = 20) Pageable pageable) {
        return PageResponse.from(contractRepository.findByBudgetId(budgetId, pageable).map(ContractResponse::from));
    }

    @Operation(summary = "Create a contract")
    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    ResponseEntity<ContractResponse> create(@PathVariable UUID budgetId, @Valid @RequestBody CreateContractRequest req) {
        Contract contract = contractService.create(budgetId, req.supplierId(), req.number(), req.description(),
                req.originalValue(), req.retentionPct(), req.startDate(), req.endDate());
        return ResponseEntity.created(URI.create("/api/v1/budgets/" + budgetId + "/contracts/" + contract.getId()))
                .body(ContractResponse.from(contract));
    }

    @Operation(summary = "Activate a contract")
    @PostMapping("/{contractId}/activate")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    ContractResponse activate(@PathVariable UUID budgetId, @PathVariable UUID contractId) {
        return ContractResponse.from(contractService.activate(contractId));
    }

    @Operation(summary = "Financial summary of a contract")
    @GetMapping("/{contractId}/financial-summary")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    ContractFinancialSummary financialSummary(@PathVariable UUID budgetId, @PathVariable UUID contractId) {
        return contractService.financialSummary(contractId);
    }

    @Operation(summary = "Add a change order (aditivo)")
    @PostMapping("/{contractId}/change-orders")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @ResponseStatus(HttpStatus.CREATED)
    ChangeOrderResponse addChangeOrder(@PathVariable UUID budgetId, @PathVariable UUID contractId,
                                       @Valid @RequestBody CreateChangeOrderRequest req) {
        ChangeOrder co = contractService.addChangeOrder(contractId, req.number(), req.description(),
                req.amount(), req.justification());
        return ChangeOrderResponse.from(co);
    }

    @Operation(summary = "Approve a change order")
    @PostMapping("/{contractId}/change-orders/{coId}/approve")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    ChangeOrderResponse approveChangeOrder(@PathVariable UUID budgetId, @PathVariable UUID contractId,
                                           @PathVariable UUID coId) {
        return ChangeOrderResponse.from(contractService.approveChangeOrder(contractId, coId));
    }

    @Operation(summary = "Reject a change order")
    @PostMapping("/{contractId}/change-orders/{coId}/reject")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    ChangeOrderResponse rejectChangeOrder(@PathVariable UUID budgetId, @PathVariable UUID contractId,
                                          @PathVariable UUID coId) {
        return ChangeOrderResponse.from(contractService.rejectChangeOrder(contractId, coId));
    }

    // --- DTOs ---
    record CreateContractRequest(@NotBlank String number, @NotBlank String description, @NotNull UUID supplierId,
                                 @NotNull BigDecimal originalValue, @NotNull BigDecimal retentionPct,
                                 @NotNull LocalDate startDate, LocalDate endDate) {}
    record CreateChangeOrderRequest(@NotNull Integer number, @NotBlank String description,
                                    @NotNull BigDecimal amount, String justification) {}

    record ContractResponse(UUID id, String number, String description, String supplierName,
                            BigDecimal originalValue, BigDecimal updatedValue, BigDecimal retentionPct,
                            ContractStatus status, LocalDate startDate, LocalDate endDate, int changeOrderCount) {
        static ContractResponse from(Contract c) {
            return new ContractResponse(c.getId(), c.getNumber(), c.getDescription(), c.getSupplier().getName(),
                    c.getOriginalValue(), c.getUpdatedValue(), c.getRetentionPct(), c.getStatus(),
                    c.getStartDate(), c.getEndDate(), c.getChangeOrders().size());
        }
    }

    record ChangeOrderResponse(UUID id, Integer number, String description, BigDecimal amount,
                               ChangeOrderStatus status, Instant approvedAt) {
        static ChangeOrderResponse from(ChangeOrder co) {
            return new ChangeOrderResponse(co.getId(), co.getNumber(), co.getDescription(),
                    co.getAmount(), co.getStatus(), co.getApprovedAt());
        }
    }
}
