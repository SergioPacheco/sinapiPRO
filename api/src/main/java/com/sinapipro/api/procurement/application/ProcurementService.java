package com.sinapipro.api.procurement.application;

import com.sinapipro.api.budget.domain.Budget;
import com.sinapipro.api.budget.domain.BudgetRepository;
import com.sinapipro.api.jobcosting.domain.CostCode;
import com.sinapipro.api.jobcosting.domain.CostCodeRepository;
import com.sinapipro.api.jobcosting.domain.CostTransaction;
import com.sinapipro.api.jobcosting.domain.CostTransactionRepository;
import com.sinapipro.api.jobcosting.domain.CostTransactionType;
import com.sinapipro.api.procurement.domain.*;
import com.sinapipro.api.shared.error.DomainNotFoundException;
import com.sinapipro.api.supplier.domain.Supplier;
import com.sinapipro.api.supplier.domain.SupplierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ProcurementService {

    private final PurchaseRequestRepository requestRepository;
    private final QuotationRepository quotationRepository;
    private final PurchaseOrderRepository orderRepository;
    private final BudgetRepository budgetRepository;
    private final SupplierRepository supplierRepository;
    private final CostCodeRepository costCodeRepository;
    private final CostTransactionRepository costTransactionRepository;

    public ProcurementService(PurchaseRequestRepository requestRepository, QuotationRepository quotationRepository,
                              PurchaseOrderRepository orderRepository, BudgetRepository budgetRepository,
                              SupplierRepository supplierRepository, CostCodeRepository costCodeRepository,
                              CostTransactionRepository costTransactionRepository) {
        this.requestRepository = requestRepository;
        this.quotationRepository = quotationRepository;
        this.orderRepository = orderRepository;
        this.budgetRepository = budgetRepository;
        this.supplierRepository = supplierRepository;
        this.costCodeRepository = costCodeRepository;
        this.costTransactionRepository = costTransactionRepository;
    }

    @Transactional
    public Quotation createQuotation(UUID purchaseRequestId, LocalDate deadline) {
        PurchaseRequest pr = requestRepository.findById(purchaseRequestId)
                .orElseThrow(() -> new DomainNotFoundException("Purchase request not found: " + purchaseRequestId));
        Quotation quotation = new Quotation(pr, deadline);
        return quotationRepository.save(quotation);
    }

    @Transactional
    public QuotationResponse addSupplierResponse(UUID quotationId, UUID supplierId,
                                                  BigDecimal unitPrice, Integer deliveryDays, String notes) {
        Quotation quotation = quotationRepository.findById(quotationId)
                .orElseThrow(() -> new DomainNotFoundException("Quotation not found: " + quotationId));
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new DomainNotFoundException("Supplier not found: " + supplierId));
        QuotationResponse response = new QuotationResponse(quotation, supplier, unitPrice, deliveryDays, notes);
        quotation.getResponses().add(response);
        quotationRepository.save(quotation);
        return response;
    }

    public ComparativeAnalysis analyze(UUID quotationId) {
        Quotation quotation = quotationRepository.findById(quotationId)
                .orElseThrow(() -> new DomainNotFoundException("Quotation not found: " + quotationId));
        List<QuotationResponse> responses = quotation.getResponses();
        if (responses.isEmpty()) {
            return new ComparativeAnalysis(List.of(), null);
        }

        List<SupplierQuote> quotes = responses.stream()
                .map(r -> new SupplierQuote(r.getId(), r.getSupplier().getName(), r.getUnitPrice(), r.getDeliveryDays()))
                .sorted(Comparator.comparing(SupplierQuote::unitPrice))
                .toList();

        return new ComparativeAnalysis(quotes, quotes.getFirst());
    }

    @Transactional
    public PurchaseOrder generateOrder(UUID quotationId, String orderNumber) {
        Quotation quotation = quotationRepository.findById(quotationId)
                .orElseThrow(() -> new DomainNotFoundException("Quotation not found: " + quotationId));

        ComparativeAnalysis analysis = analyze(quotationId);
        if (analysis.bestPrice() == null) {
            throw new IllegalStateException("No supplier responses to generate order from");
        }

        QuotationResponse best = quotation.getResponses().stream()
                .filter(r -> r.getId().equals(analysis.bestPrice().responseId()))
                .findFirst().orElseThrow();

        PurchaseRequest pr = quotation.getPurchaseRequest();
        PurchaseOrder order = new PurchaseOrder(
                pr.getBudget(), best.getSupplier(), best.getId(),
                orderNumber, pr.getDescription(), pr.getQuantity(), best.getUnitPrice(), pr.getCostCodeId());

        quotation.close();
        quotationRepository.save(quotation);
        PurchaseOrder saved = orderRepository.save(order);

        // Register COMMITTED cost transaction if cost code is linked
        if (pr.getCostCodeId() != null) {
            costCodeRepository.findById(pr.getCostCodeId()).ifPresent(costCode -> {
                CostTransaction tx = new CostTransaction(costCode, CostTransactionType.COMMITTED,
                        saved.getTotalAmount(), "PO " + orderNumber + ": " + pr.getDescription(),
                        saved.getId(), LocalDate.now());
                costTransactionRepository.save(tx);
            });
        }

        return saved;
    }

    @Transactional
    public Receiving receive(UUID orderId, BigDecimal quantityReceived, LocalDate receivedAt, String notes) {
        PurchaseOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new DomainNotFoundException("Purchase order not found: " + orderId));

        if ("RECEIVED".equals(order.getStatus())) {
            throw new IllegalStateException("Order already fully received");
        }

        Receiving receiving = new Receiving(order, quantityReceived, receivedAt, notes);
        order.getReceivings().add(receiving);

        BigDecimal totalReceived = order.getReceivedQuantity();
        if (totalReceived.compareTo(order.getQuantity()) >= 0) {
            order.markFullyReceived();
            // Register ACTUAL cost transaction on full receipt (idempotent)
            if (order.getCostCodeId() != null
                    && !costTransactionRepository.existsByReferenceIdAndType(order.getId(), CostTransactionType.ACTUAL)) {
                costCodeRepository.findById(order.getCostCodeId()).ifPresent(costCode -> {
                    CostTransaction tx = new CostTransaction(costCode, CostTransactionType.ACTUAL,
                            order.getTotalAmount(), "Received PO " + order.getNumber(),
                            order.getId(), receivedAt);
                    costTransactionRepository.save(tx);
                });
            }
        } else {
            order.markPartiallyReceived();
        }

        orderRepository.save(order);
        return receiving;
    }

    public List<PurchaseOrder> listOrders(UUID budgetId) {
        return orderRepository.findByBudgetIdOrderByCreatedAtDesc(budgetId);
    }

    public org.springframework.data.domain.Page<PurchaseOrder> listOrdersPaged(UUID budgetId, org.springframework.data.domain.Pageable pageable) {
        return orderRepository.findByBudgetId(budgetId, pageable);
    }

    public record SupplierQuote(UUID responseId, String supplierName, BigDecimal unitPrice, Integer deliveryDays) {}
    public record ComparativeAnalysis(List<SupplierQuote> quotes, SupplierQuote bestPrice) {}
}
