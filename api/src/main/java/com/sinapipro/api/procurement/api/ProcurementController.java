package com.sinapipro.api.procurement.api;

import com.sinapipro.api.budget.domain.BudgetRepository;
import com.sinapipro.api.procurement.application.ProcurementService;
import com.sinapipro.api.procurement.application.ProcurementService.*;
import com.sinapipro.api.procurement.domain.*;
import com.sinapipro.api.shared.api.PageResponse;
import com.sinapipro.api.shared.error.DomainNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Tag(name = "Procurement", description = "Purchase requests, quotations, orders and receiving")
@RestController
@RequestMapping("/api/v1/budgets/{budgetId}/procurement")
public class ProcurementController {

    private final PurchaseRequestRepository requestRepository;
    private final BudgetRepository budgetRepository;
    private final ProcurementService procurementService;

    public ProcurementController(PurchaseRequestRepository requestRepository, BudgetRepository budgetRepository,
                                 ProcurementService procurementService) {
        this.requestRepository = requestRepository;
        this.budgetRepository = budgetRepository;
        this.procurementService = procurementService;
    }

    // --- Purchase Requests ---

    @Operation(summary = "List purchase requests for a budget")
    @GetMapping("/requests")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    PageResponse<PurchaseRequestResponse> listRequests(@PathVariable UUID budgetId, @PageableDefault(size = 20) Pageable pageable) {
        return PageResponse.from(requestRepository.findByBudgetId(budgetId, pageable).map(PurchaseRequestResponse::from));
    }

    @Operation(summary = "Create a purchase request")
    @PostMapping("/requests")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    ResponseEntity<PurchaseRequestResponse> createRequest(@PathVariable UUID budgetId,
                                                          @Valid @RequestBody CreatePurchaseRequestReq req) {
        var budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new DomainNotFoundException("Budget not found: " + budgetId));
        PurchaseRequest pr = requestRepository.save(
                new PurchaseRequest(budget, req.costCodeId(), req.description(), req.quantity(), req.unit(), req.requestedBy()));
        return ResponseEntity.created(URI.create("/api/v1/budgets/" + budgetId + "/procurement/requests/" + pr.getId()))
                .body(PurchaseRequestResponse.from(pr));
    }

    // --- Quotations ---

    @Operation(summary = "Create a quotation for a purchase request")
    @PostMapping("/requests/{requestId}/quotations")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @ResponseStatus(HttpStatus.CREATED)
    QuotationSummary createQuotation(@PathVariable UUID budgetId, @PathVariable UUID requestId,
                                     @Valid @RequestBody CreateQuotationReq req) {
        Quotation q = procurementService.createQuotation(requestId, req.deadline());
        return new QuotationSummary(q.getId(), q.getStatus(), q.getDeadline(), 0);
    }

    @Operation(summary = "Add supplier response to a quotation")
    @PostMapping("/quotations/{quotationId}/responses")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @ResponseStatus(HttpStatus.CREATED)
    SupplierQuote addResponse(@PathVariable UUID budgetId, @PathVariable UUID quotationId,
                              @Valid @RequestBody AddQuotationResponseReq req) {
        QuotationResponse r = procurementService.addSupplierResponse(quotationId, req.supplierId(),
                req.unitPrice(), req.deliveryDays(), req.notes());
        return new SupplierQuote(r.getId(), r.getSupplier().getName(), r.getUnitPrice(), r.getDeliveryDays());
    }

    @Operation(summary = "Comparative analysis of quotation responses")
    @GetMapping("/quotations/{quotationId}/analysis")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    ComparativeAnalysis analyze(@PathVariable UUID budgetId, @PathVariable UUID quotationId) {
        return procurementService.analyze(quotationId);
    }

    // --- Purchase Orders ---

    @Operation(summary = "Generate purchase order from best quotation price")
    @PostMapping("/quotations/{quotationId}/generate-order")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @ResponseStatus(HttpStatus.CREATED)
    PurchaseOrderResponse generateOrder(@PathVariable UUID budgetId, @PathVariable UUID quotationId,
                                        @Valid @RequestBody GenerateOrderReq req) {
        PurchaseOrder order = procurementService.generateOrder(quotationId, req.orderNumber());
        return PurchaseOrderResponse.from(order);
    }

    @Operation(summary = "List purchase orders for a budget")
    @GetMapping("/orders")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    PageResponse<PurchaseOrderResponse> listOrders(@PathVariable UUID budgetId, @PageableDefault(size = 20) Pageable pageable) {
        return PageResponse.from(procurementService.listOrdersPaged(budgetId, pageable).map(PurchaseOrderResponse::from));
    }

    // --- Receiving ---

    @Operation(summary = "Register receiving for a purchase order")
    @PostMapping("/orders/{orderId}/receive")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @ResponseStatus(HttpStatus.CREATED)
    ReceivingResponse receive(@PathVariable UUID budgetId, @PathVariable UUID orderId,
                              @Valid @RequestBody ReceiveReq req) {
        Receiving r = procurementService.receive(orderId, req.quantityReceived(), req.receivedAt(), req.notes());
        return new ReceivingResponse(r.getId(), r.getQuantityReceived(), r.getReceivedAt(), r.getNotes());
    }

    // --- DTOs ---
    record CreatePurchaseRequestReq(@NotBlank String description, @NotNull @Positive BigDecimal quantity,
                                    @NotBlank String unit, UUID costCodeId, String requestedBy) {}
    record CreateQuotationReq(LocalDate deadline) {}
    record AddQuotationResponseReq(@NotNull UUID supplierId, @NotNull @Positive BigDecimal unitPrice,
                                   Integer deliveryDays, String notes) {}
    record GenerateOrderReq(@NotBlank String orderNumber) {}
    record ReceiveReq(@NotNull @Positive BigDecimal quantityReceived, @NotNull LocalDate receivedAt, String notes) {}

    record PurchaseRequestResponse(UUID id, String description, BigDecimal quantity, String unit,
                                   PurchaseRequestStatus status, String requestedBy, UUID costCodeId) {
        static PurchaseRequestResponse from(PurchaseRequest pr) {
            return new PurchaseRequestResponse(pr.getId(), pr.getDescription(), pr.getQuantity(),
                    pr.getUnit(), pr.getStatus(), pr.getRequestedBy(), pr.getCostCodeId());
        }
    }

    record QuotationSummary(UUID id, String status, LocalDate deadline, int responseCount) {}
    record SupplierQuote(UUID responseId, String supplierName, BigDecimal unitPrice, Integer deliveryDays) {}

    record PurchaseOrderResponse(UUID id, String number, String description, String supplierName,
                                 BigDecimal quantity, BigDecimal unitPrice, BigDecimal totalAmount, String status) {
        static PurchaseOrderResponse from(PurchaseOrder po) {
            return new PurchaseOrderResponse(po.getId(), po.getNumber(), po.getDescription(),
                    po.getSupplier().getName(), po.getQuantity(), po.getUnitPrice(), po.getTotalAmount(), po.getStatus());
        }
    }

    record ReceivingResponse(UUID id, BigDecimal quantityReceived, LocalDate receivedAt, String notes) {}
}
