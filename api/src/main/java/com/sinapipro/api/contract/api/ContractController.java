package com.sinapipro.api.contract.api;

import com.sinapipro.api.contract.application.ContractReportService;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
@RequestMapping("/api/v1/projects/{projectId}/contracts")
public class ContractController {

    private final ContractService contractService;
    private final ContractRepository contractRepository;
    private final ContractReportService contractReportService;

    public ContractController(ContractService contractService, ContractRepository contractRepository,
                              ContractReportService contractReportService) {
        this.contractService = contractService;
        this.contractRepository = contractRepository;
        this.contractReportService = contractReportService;
    }

    @Operation(summary = "List contracts for a budget")
    @GetMapping
    @PreAuthorize("@perm.check('budget.read')")
    PageResponse<ContractResponse> list(@PathVariable UUID projectId, @PageableDefault(size = 20) Pageable pageable) {
        return PageResponse.from(contractRepository.findByBudgetId(projectId, pageable).map(ContractResponse::from));
    }

    @Operation(summary = "Create a contract")
    @PostMapping
    @PreAuthorize("@perm.check('budget.write')")
    ResponseEntity<ContractResponse> create(@PathVariable UUID projectId, @Valid @RequestBody CreateContractRequest req) {
        Contract contract = contractService.create(projectId, req.supplierId(), req.number(), req.description(),
                req.originalValue(), req.retentionPct(), req.startDate(), req.endDate());
        return ResponseEntity.created(URI.create("/api/v1/budgets/" + projectId + "/contracts/" + contract.getId()))
                .body(ContractResponse.from(contract));
    }

    @Operation(summary = "Activate a contract")
    @PostMapping("/{contractId}/activate")
    @PreAuthorize("@perm.check('budget.write')")
    ContractResponse activate(@PathVariable UUID projectId, @PathVariable UUID contractId) {
        return ContractResponse.from(contractService.activate(contractId));
    }

    @Operation(summary = "Financial summary of a contract")
    @GetMapping("/{contractId}/financial-summary")
    @PreAuthorize("@perm.check('budget.read')")
    ContractFinancialSummary financialSummary(@PathVariable UUID projectId, @PathVariable UUID contractId) {
        return contractService.financialSummary(contractId);
    }

    @Operation(summary = "Add a change order (aditivo)")
    @PostMapping("/{contractId}/change-orders")
    @PreAuthorize("@perm.check('budget.write')")
    @ResponseStatus(HttpStatus.CREATED)
    ChangeOrderResponse addChangeOrder(@PathVariable UUID projectId, @PathVariable UUID contractId,
                                       @Valid @RequestBody CreateChangeOrderRequest req) {
        ChangeOrder co = contractService.addChangeOrder(contractId, req.number(), req.description(),
                req.amount(), req.justification());
        return ChangeOrderResponse.from(co);
    }

    @Operation(summary = "Approve a change order")
    @PostMapping("/{contractId}/change-orders/{coId}/approve")
    @PreAuthorize("@perm.check('budget.write')")
    ChangeOrderResponse approveChangeOrder(@PathVariable UUID projectId, @PathVariable UUID contractId,
                                           @PathVariable UUID coId) {
        return ChangeOrderResponse.from(contractService.approveChangeOrder(contractId, coId));
    }

    @Operation(summary = "Reject a change order")
    @PostMapping("/{contractId}/change-orders/{coId}/reject")
    @PreAuthorize("@perm.check('budget.write')")
    ChangeOrderResponse rejectChangeOrder(@PathVariable UUID projectId, @PathVariable UUID contractId,
                                          @PathVariable UUID coId) {
        return ChangeOrderResponse.from(contractService.rejectChangeOrder(contractId, coId));
    }

    @Operation(summary = "Contract report PDF")
    @GetMapping(value = "/{contractId}/reports/contract.pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @PreAuthorize("@perm.check('budget.read')")
    ResponseEntity<byte[]> contractReport(@PathVariable UUID projectId, @PathVariable UUID contractId) {
        byte[] pdf = contractReportService.generateContractPdf(contractId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=contract-" + contractId + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
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
