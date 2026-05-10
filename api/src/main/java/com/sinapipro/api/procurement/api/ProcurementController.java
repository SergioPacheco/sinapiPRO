package com.sinapipro.api.procurement.api;

import com.sinapipro.api.budget.domain.BudgetRepository;
import com.sinapipro.api.procurement.application.ProcurementReportService;
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

@Tag(name = "Procurement", description = "Purchase requests, quotations, orders and receiving")
@RestController
@RequestMapping("/api/v1/projects/{projectId}/procurement")
public class ProcurementController {

    private final PurchaseRequestRepository requestRepository;
    private final BudgetRepository budgetRepository;
    private final ProcurementService procurementService;
    private final ProcurementReportService procurementReportService;
    private final PurchaseOrderCostDistributionRepository costDistributionRepository;
    private final PurchaseOrderRepository orderRepository;
    private final QuotationEmailRepository quotationEmailRepository;

    public ProcurementController(PurchaseRequestRepository requestRepository, BudgetRepository budgetRepository,
                                 ProcurementService procurementService, ProcurementReportService procurementReportService,
                                 PurchaseOrderCostDistributionRepository costDistributionRepository,
                                 PurchaseOrderRepository orderRepository,
                                 QuotationEmailRepository quotationEmailRepository) {
        this.requestRepository = requestRepository;
        this.budgetRepository = budgetRepository;
        this.procurementService = procurementService;
        this.procurementReportService = procurementReportService;
        this.costDistributionRepository = costDistributionRepository;
        this.orderRepository = orderRepository;
        this.quotationEmailRepository = quotationEmailRepository;
    }

    // --- Purchase Requests ---

    @Operation(summary = "List purchase requests for a budget")
    @GetMapping("/requests")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    PageResponse<PurchaseRequestResponse> listRequests(@PathVariable UUID projectId, @PageableDefault(size = 20) Pageable pageable) {
        return PageResponse.from(requestRepository.findByBudgetId(projectId, pageable).map(PurchaseRequestResponse::from));
    }

    @Operation(summary = "Create a purchase request")
    @PostMapping("/requests")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    ResponseEntity<PurchaseRequestResponse> createRequest(@PathVariable UUID projectId,
                                                          @Valid @RequestBody CreatePurchaseRequestReq req) {
        var budget = budgetRepository.findById(projectId)
                .orElseThrow(() -> new DomainNotFoundException("Budget not found: " + projectId));
        PurchaseRequest pr = requestRepository.save(
                new PurchaseRequest(budget, req.costCodeId(), req.description(), req.quantity(), req.unit(), req.requestedBy()));
        return ResponseEntity.created(URI.create("/api/v1/budgets/" + projectId + "/procurement/requests/" + pr.getId()))
                .body(PurchaseRequestResponse.from(pr));
    }

    // --- Quotations ---

    @Operation(summary = "Create a quotation for a purchase request")
    @PostMapping("/requests/{requestId}/quotations")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @ResponseStatus(HttpStatus.CREATED)
    QuotationSummary createQuotation(@PathVariable UUID projectId, @PathVariable UUID requestId,
                                     @Valid @RequestBody CreateQuotationReq req) {
        Quotation q = procurementService.createQuotation(requestId, req.deadline());
        return new QuotationSummary(q.getId(), q.getStatus(), q.getDeadline(), 0);
    }

    @Operation(summary = "Add supplier response to a quotation")
    @PostMapping("/quotations/{quotationId}/responses")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @ResponseStatus(HttpStatus.CREATED)
    SupplierQuote addResponse(@PathVariable UUID projectId, @PathVariable UUID quotationId,
                              @Valid @RequestBody AddQuotationResponseReq req) {
        QuotationResponse r = procurementService.addSupplierResponse(quotationId, req.supplierId(),
                req.unitPrice(), req.deliveryDays(), req.notes());
        return new SupplierQuote(r.getId(), r.getSupplier().getName(), r.getUnitPrice(), r.getDeliveryDays());
    }

    @Operation(summary = "Comparative analysis of quotation responses")
    @GetMapping("/quotations/{quotationId}/analysis")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    ComparativeAnalysis analyze(@PathVariable UUID projectId, @PathVariable UUID quotationId) {
        return procurementService.analyze(quotationId);
    }

    // --- Purchase Orders ---

    @Operation(summary = "Generate purchase order from best quotation price")
    @PostMapping("/quotations/{quotationId}/generate-order")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @ResponseStatus(HttpStatus.CREATED)
    PurchaseOrderResponse generateOrder(@PathVariable UUID projectId, @PathVariable UUID quotationId,
                                        @Valid @RequestBody GenerateOrderReq req) {
        PurchaseOrder order = procurementService.generateOrder(quotationId, req.orderNumber());
        return PurchaseOrderResponse.from(order);
    }

    @Operation(summary = "List purchase orders for a budget")
    @GetMapping("/orders")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    PageResponse<PurchaseOrderResponse> listOrders(@PathVariable UUID projectId, @PageableDefault(size = 20) Pageable pageable) {
        return PageResponse.from(procurementService.listOrdersPaged(projectId, pageable).map(PurchaseOrderResponse::from));
    }

    // --- Receiving ---

    @Operation(summary = "Register receiving for a purchase order")
    @PostMapping("/orders/{orderId}/receive")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @ResponseStatus(HttpStatus.CREATED)
    ReceivingResponse receive(@PathVariable UUID projectId, @PathVariable UUID orderId,
                              @Valid @RequestBody ReceiveReq req) {
        Receiving r = procurementService.receive(orderId, req.quantityReceived(), req.receivedAt(), req.notes());
        return new ReceivingResponse(r.getId(), r.getQuantityReceived(), r.getReceivedAt(), r.getNotes());
    }

    // --- Order Approval ---

    @Operation(summary = "Approve a purchase order")
    @PostMapping("/orders/{orderId}/approve")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    PurchaseOrderResponse approveOrder(@PathVariable UUID projectId, @PathVariable UUID orderId) {
        return PurchaseOrderResponse.from(procurementService.approveOrder(orderId));
    }

    @Operation(summary = "Reject a purchase order")
    @PostMapping("/orders/{orderId}/reject")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    PurchaseOrderResponse rejectOrder(@PathVariable UUID projectId, @PathVariable UUID orderId) {
        return PurchaseOrderResponse.from(procurementService.rejectOrder(orderId));
    }

    // --- Overdue Orders ---

    @Operation(summary = "List overdue purchase orders (past expected delivery and not fully received)")
    @GetMapping("/orders/overdue")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    List<PurchaseOrderResponse> overdueOrders(@PathVariable UUID projectId) {
        return procurementService.findOverdueOrders(projectId).stream().map(PurchaseOrderResponse::from).toList();
    }

    // --- Reports ---

    @Operation(summary = "Quotation comparative map PDF")
    @GetMapping(value = "/quotations/{quotationId}/reports/comparative-map.pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    ResponseEntity<byte[]> comparativeMapReport(@PathVariable UUID projectId, @PathVariable UUID quotationId) {
        byte[] pdf = procurementReportService.generateComparativeMapPdf(quotationId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=comparative-map-" + quotationId + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @Operation(summary = "Purchase order PDF")
    @GetMapping(value = "/orders/{orderId}/reports/order.pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    ResponseEntity<byte[]> orderReport(@PathVariable UUID projectId, @PathVariable UUID orderId) {
        byte[] pdf = procurementReportService.generatePurchaseOrderPdf(orderId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=purchase-order-" + orderId + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    // --- Cost Distribution ---

    @Operation(summary = "Set cost code distribution for a purchase order")
    @PostMapping("/orders/{orderId}/cost-distribution")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @ResponseStatus(HttpStatus.CREATED)
    List<CostDistributionResponse> setCostDistribution(@PathVariable UUID projectId, @PathVariable UUID orderId,
                                                       @Valid @RequestBody List<CostDistributionRequest> distributions) {
        var order = orderRepository.findById(orderId)
                .orElseThrow(() -> new com.sinapipro.api.shared.error.DomainNotFoundException("Order not found: " + orderId));
        costDistributionRepository.findByPurchaseOrderId(orderId).forEach(costDistributionRepository::delete);
        return distributions.stream().map(d -> {
            var amount = order.getTotalAmount().multiply(d.percentage());
            var dist = costDistributionRepository.save(new PurchaseOrderCostDistribution(order, d.costCodeId(), d.percentage(), amount));
            return new CostDistributionResponse(dist.getId(), dist.getCostCodeId(), dist.getPercentage(), dist.getAmount());
        }).toList();
    }

    @Operation(summary = "Get cost distribution for a purchase order")
    @GetMapping("/orders/{orderId}/cost-distribution")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    List<CostDistributionResponse> getCostDistribution(@PathVariable UUID projectId, @PathVariable UUID orderId) {
        return costDistributionRepository.findByPurchaseOrderId(orderId).stream()
                .map(d -> new CostDistributionResponse(d.getId(), d.getCostCodeId(), d.getPercentage(), d.getAmount())).toList();
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

    record CostDistributionRequest(@NotNull UUID costCodeId, @NotNull BigDecimal percentage) {}
    record CostDistributionResponse(UUID id, UUID costCodeId, BigDecimal percentage, BigDecimal amount) {}

    // Task 5.2 — Gerar pedido a partir da Curva ABC do orçamento
    @Operation(summary = "Generate purchase request from ABC curve — top materials by cost impact")
    @PostMapping("/from-abc")
    @ResponseStatus(HttpStatus.CREATED)
    java.util.List<java.util.Map<String, Object>> generateFromAbc(@PathVariable UUID projectId, @RequestBody FromAbcRequest req) {
        var budget = budgetRepository.findById(projectId).orElseThrow();
        var results = new java.util.ArrayList<java.util.Map<String, Object>>();
        for (var item : req.items()) {
            var pr = new PurchaseRequest(budget, null, item.description(), item.quantity(), item.unit(), "ABC_IMPORT");
            pr = requestRepository.save(pr);
            results.add(java.util.Map.of("id", pr.getId(), "description", pr.getDescription(), "quantity", pr.getQuantity()));
        }
        return results;
    }

    record FromAbcRequest(java.util.List<AbcItem> items) {}
    record AbcItem(String description, BigDecimal quantity, String unit) {}

    @Operation(summary = "Send quotation to suppliers via email")
    @PostMapping("/quotations/{quotationId}/send-email")
    java.util.Map<String, Object> sendQuotationEmail(@PathVariable UUID projectId, @PathVariable UUID quotationId) {
        var emails = quotationEmailRepository.findByQuotationId(quotationId);
        emails.forEach(QuotationEmail::markSent);
        quotationEmailRepository.saveAll(emails);
        return java.util.Map.of("sent", emails.size(), "quotationId", quotationId);
    }
}
