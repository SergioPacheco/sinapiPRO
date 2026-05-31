package com.sinapipro.api.procurement.application;

import module java.base;

import com.sinapipro.api.budget.domain.Budget;
import com.sinapipro.api.budget.domain.BudgetRepository;
import com.sinapipro.api.jobcosting.domain.CostCode;
import com.sinapipro.api.jobcosting.domain.CostCodeRepository;
import com.sinapipro.api.jobcosting.domain.CostTransaction;
import com.sinapipro.api.jobcosting.domain.CostTransactionRepository;
import com.sinapipro.api.jobcosting.domain.CostTransactionType;
import com.sinapipro.api.procurement.domain.*;
import com.sinapipro.api.inventory.domain.StockItem;
import com.sinapipro.api.inventory.domain.StockItemRepository;
import com.sinapipro.api.inventory.domain.StockMovement;
import com.sinapipro.api.inventory.domain.StockMovementRepository;
import com.sinapipro.api.shared.error.DomainNotFoundException;
import com.sinapipro.api.supplier.domain.Supplier;
import com.sinapipro.api.supplier.domain.SupplierRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.micrometer.observation.annotation.Observed;

@Service
@Observed(name = "procurement.service")
@Transactional(readOnly = true)
public class ProcurementService {

    private final PurchaseRequestRepository requestRepository;
    private final QuotationRepository quotationRepository;
    private final PurchaseOrderRepository orderRepository;
    private final BudgetRepository budgetRepository;
    private final SupplierRepository supplierRepository;
    private final CostCodeRepository costCodeRepository;
    private final CostTransactionRepository costTransactionRepository;
    private final StockItemRepository stockItemRepository;
    private final StockMovementRepository stockMovementRepository;

    public ProcurementService(PurchaseRequestRepository requestRepository, QuotationRepository quotationRepository,
                              PurchaseOrderRepository orderRepository, BudgetRepository budgetRepository,
                              SupplierRepository supplierRepository, CostCodeRepository costCodeRepository,
                              CostTransactionRepository costTransactionRepository,
                              StockItemRepository stockItemRepository, StockMovementRepository stockMovementRepository) {
        this.requestRepository = requestRepository;
        this.quotationRepository = quotationRepository;
        this.orderRepository = orderRepository;
        this.budgetRepository = budgetRepository;
        this.supplierRepository = supplierRepository;
        this.costCodeRepository = costCodeRepository;
        this.costTransactionRepository = costTransactionRepository;
        this.stockItemRepository = stockItemRepository;
        this.stockMovementRepository = stockMovementRepository;
    }

    @Transactional
    public Quotation createQuotation(UUID purchaseRequestId, LocalDate deadline) {
        var pr = requestRepository.findById(purchaseRequestId)
                .orElseThrow(() -> new DomainNotFoundException("Purchase request not found: " + purchaseRequestId));
        var quotation = new Quotation(pr, deadline);
        return quotationRepository.save(quotation);
    }

    @Transactional
    public QuotationResponse addSupplierResponse(UUID quotationId, UUID supplierId,
                                                  BigDecimal unitPrice, Integer deliveryDays, String notes) {
        var quotation = quotationRepository.findById(quotationId)
                .orElseThrow(() -> new DomainNotFoundException("Quotation not found: " + quotationId));
        var supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new DomainNotFoundException("Supplier not found: " + supplierId));
        var response = new QuotationResponse(quotation, supplier, unitPrice, deliveryDays, notes);
        quotation.getResponses().add(response);
        quotationRepository.save(quotation);
        return response;
    }

    public ComparativeAnalysis analyze(UUID quotationId) {
        var quotation = quotationRepository.findById(quotationId)
                .orElseThrow(() -> new DomainNotFoundException("Quotation not found: " + quotationId));
        var responses = quotation.getResponses();
        if (responses.isEmpty()) {
            return new ComparativeAnalysis(List.of(), null);
        }

        var quotes = responses.stream()
                .map(r -> new SupplierQuote(r.getId(), r.getSupplier().getName(), r.getUnitPrice(), r.getDeliveryDays()))
                .sorted(Comparator.comparing(SupplierQuote::unitPrice))
                .toList();

        return new ComparativeAnalysis(quotes, quotes.getFirst());
    }

    @Transactional
    public PurchaseOrder generateOrder(UUID quotationId, String orderNumber) {
        var quotation = quotationRepository.findById(quotationId)
                .orElseThrow(() -> new DomainNotFoundException("Quotation not found: " + quotationId));

        var analysis = analyze(quotationId);
        if (analysis.bestPrice() == null) {
            throw new IllegalStateException("No supplier responses to generate order from");
        }

        var best = quotation.getResponses().stream()
                .filter(r -> r.getId().equals(analysis.bestPrice().responseId()))
                .findFirst().orElseThrow();

        var pr = quotation.getPurchaseRequest();
        var order = new PurchaseOrder(
                pr.getBudget(), best.getSupplier(), best.getId(),
                orderNumber, pr.getDescription(), pr.getQuantity(), best.getUnitPrice(), pr.getCostCodeId());

        // Close quotation and register cost transaction in parallel using Structured Concurrency
        try (var scope = StructuredTaskScope.open()) {
            scope.fork(() -> {
                quotation.close();
                quotationRepository.save(quotation);
                return null;
            });

            var savedOrder = orderRepository.save(order);

            scope.fork(() -> {
                if (pr.getCostCodeId() != null) {
                    costCodeRepository.findById(pr.getCostCodeId()).ifPresent(costCode -> {
                        var tx = new CostTransaction(costCode, CostTransactionType.COMMITTED,
                                savedOrder.getTotalAmount(), "PO " + orderNumber + ": " + pr.getDescription(),
                                savedOrder.getId(), LocalDate.now());
                        costTransactionRepository.save(tx);
                    });
                }
                return null;
            });

            scope.join();
            return savedOrder;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Order generation interrupted", e);
        }
    }

    @Transactional
    public Receiving receive(UUID orderId, BigDecimal quantityReceived, LocalDate receivedAt, String notes) {
        var order = orderRepository.findById(orderId)
                .orElseThrow(() -> new DomainNotFoundException("Purchase order not found: " + orderId));

        if ("RECEIVED".equals(order.getStatus())) {
            throw new IllegalStateException("Order already fully received");
        }

        var receiving = new Receiving(order, quantityReceived, receivedAt, notes);
        order.getReceivings().add(receiving);

        var totalReceived = order.getReceivedQuantity();
        if (totalReceived.compareTo(order.getQuantity()) >= 0) {
            order.markFullyReceived();
            if (order.getCostCodeId() != null
                    && !costTransactionRepository.existsByReferenceIdAndType(order.getId(), CostTransactionType.ACTUAL)) {
                costCodeRepository.findById(order.getCostCodeId()).ifPresent(costCode -> {
                    var tx = new CostTransaction(costCode, CostTransactionType.ACTUAL,
                            order.getTotalAmount(), "Received PO " + order.getNumber(),
                            order.getId(), receivedAt);
                    costTransactionRepository.save(tx);
                });
            }
        } else {
            order.markPartiallyReceived();
        }

        orderRepository.save(order);

        // Auto-update inventory: find or create stock item and record entry
        var budgetId = order.getBudget().getId();
        var stockItem = stockItemRepository.findByBudgetIdAndDescription(budgetId, order.getDescription())
                .orElseGet(() -> stockItemRepository.save(new StockItem(budgetId, order.getDescription(), "UN", BigDecimal.ZERO, null)));
        stockItem.addQuantity(quantityReceived);
        stockItemRepository.save(stockItem);
        stockMovementRepository.save(new StockMovement(stockItem, "IN", quantityReceived, orderId, "PURCHASE_ORDER",
                "Recebimento PO " + order.getNumber()));

        return receiving;
    }

    public List<PurchaseOrder> listOrders(UUID budgetId) {
        return orderRepository.findByBudgetIdOrderByCreatedAtDesc(budgetId);
    }

    public Page<PurchaseOrder> listOrdersPaged(UUID budgetId, Pageable pageable) {
        return orderRepository.findByBudgetId(budgetId, pageable);
    }

    public Page<Quotation> listQuotationsPaged(UUID budgetId, Pageable pageable) {
        return quotationRepository.findByPurchaseRequestBudgetId(budgetId, pageable);
    }

    public Page<Quotation> listQuotationsByOrderPaged(UUID budgetId, UUID orderId, Pageable pageable) {
        var order = orderRepository.findById(orderId)
                .orElseThrow(() -> new DomainNotFoundException("Purchase order not found: " + orderId));
        if (!budgetId.equals(order.getBudget().getId())) {
            throw new DomainNotFoundException("Purchase order not found in project: " + orderId);
        }
        if (order.getQuotationResponseId() == null) {
            return Page.empty(pageable);
        }
        return quotationRepository.findByBudgetIdAndResponseId(budgetId, order.getQuotationResponseId(), pageable);
    }

    @Transactional
    public PurchaseOrder approveOrder(UUID orderId) {
        var order = orderRepository.findById(orderId)
                .orElseThrow(() -> new DomainNotFoundException("Purchase order not found: " + orderId));
        order.approve();
        return orderRepository.save(order);
    }

    @Transactional
    public PurchaseOrder rejectOrder(UUID orderId) {
        var order = orderRepository.findById(orderId)
                .orElseThrow(() -> new DomainNotFoundException("Purchase order not found: " + orderId));
        order.reject();
        return orderRepository.save(order);
    }

    public List<PurchaseOrder> findOverdueOrders(UUID budgetId) {
        return orderRepository.findOverdue(budgetId, LocalDate.now());
    }

    public record SupplierQuote(UUID responseId, String supplierName, BigDecimal unitPrice, Integer deliveryDays) {}
    public record ComparativeAnalysis(List<SupplierQuote> quotes, SupplierQuote bestPrice) {}
}
