package com.sinapipro.api.finance.api;

import com.sinapipro.api.finance.application.BudgetVsActualReportService;
import com.sinapipro.api.finance.application.BudgetVsActualService;
import com.sinapipro.api.finance.application.BudgetVsActualService.*;
import com.sinapipro.api.finance.application.FinanceService;
import com.sinapipro.api.finance.application.FinanceService.*;
import com.sinapipro.api.finance.domain.*;
import com.sinapipro.api.shared.api.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Tag(name = "Finance", description = "Accounts payable, receivable and cash flow")
@RestController
@RequestMapping("/api/v1/projects/{projectId}/finance")
public class FinanceController {

    private final FinanceService financeService;
    private final BudgetVsActualService budgetVsActualService;
    private final BudgetVsActualReportService budgetVsActualReportService;

    public FinanceController(FinanceService financeService, BudgetVsActualService budgetVsActualService,
                             BudgetVsActualReportService budgetVsActualReportService) {
        this.financeService = financeService;
        this.budgetVsActualService = budgetVsActualService;
        this.budgetVsActualReportService = budgetVsActualReportService;
    }

    // --- Payables ---

    @Operation(summary = "List accounts payable")
    @GetMapping("/payables")
    @PreAuthorize("@perm.check('finance.read')")
    PageResponse<PayableResponse> listPayables(@PathVariable UUID projectId, @PageableDefault(size = 20) Pageable pageable) {
        return PageResponse.from(financeService.listPayables(projectId, pageable).map(PayableResponse::from));
    }

    @Operation(summary = "Create an account payable")
    @PostMapping("/payables")
    @PreAuthorize("@perm.check('finance.write')")
    ResponseEntity<PayableResponse> createPayable(@PathVariable UUID projectId, @Valid @RequestBody CreatePayableRequest req) {
        var payable = financeService.createPayable(projectId, req.supplierId(), req.description(), req.amount(),
                req.dueDate(), req.category(), req.purchaseOrderId(), req.measurementId(), req.notes());
        return ResponseEntity.created(URI.create("/api/v1/projects/" + projectId + "/finance/payables/" + payable.getId()))
                .body(PayableResponse.from(payable));
    }

    @Operation(summary = "Pay an account payable")
    @PostMapping("/payables/{id}/pay")
    @PreAuthorize("@perm.check('finance.pay')")
    PayableResponse payPayable(@PathVariable UUID projectId, @PathVariable UUID id, @Valid @RequestBody PayRequest req) {
        return PayableResponse.from(financeService.payPayable(id, req.amount(), req.date()));
    }

    @Operation(summary = "Cancel an account payable")
    @PostMapping("/payables/{id}/cancel")
    @PreAuthorize("@perm.check('finance.write')")
    PayableResponse cancelPayable(@PathVariable UUID projectId, @PathVariable UUID id) {
        return PayableResponse.from(financeService.cancelPayable(id));
    }

    @Operation(summary = "List overdue payables")
    @GetMapping("/payables/overdue")
    @PreAuthorize("@perm.check('finance.read')")
    List<PayableResponse> overduePayables(@PathVariable UUID projectId) {
        return financeService.overduePayables(projectId).stream().map(PayableResponse::from).toList();
    }

    // --- Receivables ---

    @Operation(summary = "List accounts receivable")
    @GetMapping("/receivables")
    @PreAuthorize("@perm.check('finance.read')")
    PageResponse<ReceivableResponse> listReceivables(@PathVariable UUID projectId, @PageableDefault(size = 20) Pageable pageable) {
        return PageResponse.from(financeService.listReceivables(projectId, pageable).map(ReceivableResponse::from));
    }

    @Operation(summary = "Create an account receivable")
    @PostMapping("/receivables")
    @PreAuthorize("@perm.check('finance.write')")
    ResponseEntity<ReceivableResponse> createReceivable(@PathVariable UUID projectId, @Valid @RequestBody CreateReceivableRequest req) {
        var receivable = financeService.createReceivable(projectId, req.description(), req.amount(),
                req.dueDate(), req.category(), req.measurementId(), req.invoiceId(), req.notes());
        return ResponseEntity.created(URI.create("/api/v1/projects/" + projectId + "/finance/receivables/" + receivable.getId()))
                .body(ReceivableResponse.from(receivable));
    }

    @Operation(summary = "Register payment received")
    @PostMapping("/receivables/{id}/receive")
    @PreAuthorize("@perm.check('finance.receive')")
    ReceivableResponse receivePayment(@PathVariable UUID projectId, @PathVariable UUID id, @Valid @RequestBody PayRequest req) {
        return ReceivableResponse.from(financeService.receivePayment(id, req.amount(), req.date()));
    }

    @Operation(summary = "Cancel an account receivable")
    @PostMapping("/receivables/{id}/cancel")
    @PreAuthorize("@perm.check('finance.write')")
    ReceivableResponse cancelReceivable(@PathVariable UUID projectId, @PathVariable UUID id) {
        return ReceivableResponse.from(financeService.cancelReceivable(id));
    }

    @Operation(summary = "List overdue receivables")
    @GetMapping("/receivables/overdue")
    @PreAuthorize("@perm.check('finance.read')")
    List<ReceivableResponse> overdueReceivables(@PathVariable UUID projectId) {
        return financeService.overdueReceivables(projectId).stream().map(ReceivableResponse::from).toList();
    }

    // --- Cash Flow ---

    @Operation(summary = "Cash flow summary (current balance and projections)")
    @GetMapping("/cash-flow/summary")
    @PreAuthorize("@perm.check('finance.read')")
    CashFlowSummary cashFlowSummary(@PathVariable UUID projectId) {
        return financeService.cashFlowSummary(projectId);
    }

    @Operation(summary = "Cash flow projection by month")
    @GetMapping("/cash-flow/projection")
    @PreAuthorize("@perm.check('finance.read')")
    CashFlowProjection cashFlowProjection(@PathVariable UUID projectId,
                                           @RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {
        return financeService.cashFlowProjection(projectId, startDate, endDate);
    }

    @Operation(summary = "Consolidated cash flow across multiple projects")
    @GetMapping("/cash-flow/consolidated")
    @PreAuthorize("@perm.check('finance.read')")
    FinanceService.ConsolidatedCashFlow consolidatedCashFlow(@PathVariable UUID projectId,
                                                             @RequestParam List<UUID> projectIds) {
        return financeService.consolidatedCashFlow(projectIds);
    }

    // --- Budget vs Actual ---

    @Operation(summary = "Consolidated budget vs actual report (per cost code)")
    @GetMapping("/budget-vs-actual")
    @PreAuthorize("@perm.check('finance.read')")
    BudgetVsActualReport budgetVsActual(@PathVariable UUID projectId) {
        return budgetVsActualService.consolidatedReport(projectId);
    }

    @Operation(summary = "Cost by input/composition (budgeted vs actual per service)")
    @GetMapping("/cost-by-input")
    @PreAuthorize("@perm.check('finance.read')")
    List<CostByInputLine> costByInput(@PathVariable UUID projectId) {
        return budgetVsActualService.costByInput(projectId);
    }

    @Operation(summary = "Cost by period (actual and committed grouped by month)")
    @GetMapping("/cost-by-period")
    @PreAuthorize("@perm.check('finance.read')")
    CostByPeriodReport costByPeriod(@PathVariable UUID projectId) {
        return budgetVsActualService.costByPeriod(projectId);
    }

    @Operation(summary = "Budget vs actual PDF report")
    @GetMapping(value = "/budget-vs-actual/reports/report.pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @PreAuthorize("@perm.check('finance.read')")
    ResponseEntity<byte[]> budgetVsActualPdf(@PathVariable UUID projectId) {
        byte[] pdf = budgetVsActualReportService.generatePdf(projectId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=budget-vs-actual-" + projectId + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    // --- DTOs ---
    record CreatePayableRequest(@NotBlank String description, @NotNull @Positive BigDecimal amount,
                                @NotNull LocalDate dueDate, String category, UUID supplierId,
                                UUID purchaseOrderId, UUID measurementId, String notes) {}

    record CreateReceivableRequest(@NotBlank String description, @NotNull @Positive BigDecimal amount,
                                   @NotNull LocalDate dueDate, String category,
                                   UUID measurementId, UUID invoiceId, String notes) {}

    record PayRequest(@NotNull @Positive BigDecimal amount, @NotNull LocalDate date) {}

    record PayableResponse(UUID id, String description, BigDecimal amount, LocalDate dueDate,
                           LocalDate paidDate, BigDecimal paidAmount, PaymentStatus status,
                           String category, UUID supplierId, UUID purchaseOrderId, UUID measurementId) {
        static PayableResponse from(Payable p) {
            return new PayableResponse(p.getId(), p.getDescription(), p.getAmount(), p.getDueDate(),
                    p.getPaidDate(), p.getPaidAmount(), p.getStatus(), p.getCategory(),
                    p.getSupplierId(), p.getPurchaseOrderId(), p.getMeasurementId());
        }
    }

    record ReceivableResponse(UUID id, String description, BigDecimal amount, LocalDate dueDate,
                              LocalDate receivedDate, BigDecimal receivedAmount, PaymentStatus status,
                              String category, UUID measurementId, UUID invoiceId) {
        static ReceivableResponse from(Receivable r) {
            return new ReceivableResponse(r.getId(), r.getDescription(), r.getAmount(), r.getDueDate(),
                    r.getReceivedDate(), r.getReceivedAmount(), r.getStatus(), r.getCategory(),
                    r.getMeasurementId(), r.getInvoiceId());
        }
    }
}
