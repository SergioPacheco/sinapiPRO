package com.sinapipro.api.procurement;

import com.sinapipro.api.budget.domain.Budget;
import com.sinapipro.api.budget.domain.BudgetRepository;
import com.sinapipro.api.inventory.domain.StockItemRepository;
import com.sinapipro.api.inventory.domain.StockMovementRepository;
import com.sinapipro.api.jobcosting.domain.CostCodeRepository;
import com.sinapipro.api.jobcosting.domain.CostTransactionRepository;
import com.sinapipro.api.procurement.application.ProcurementService;
import com.sinapipro.api.procurement.application.ProcurementService.ComparativeAnalysis;
import com.sinapipro.api.procurement.domain.*;
import com.sinapipro.api.supplier.domain.Supplier;
import com.sinapipro.api.supplier.domain.SupplierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcurementServiceTest {

    @Mock PurchaseRequestRepository requestRepository;
    @Mock QuotationRepository quotationRepository;
    @Mock PurchaseOrderRepository orderRepository;
    @Mock BudgetRepository budgetRepository;
    @Mock SupplierRepository supplierRepository;
    @Mock CostCodeRepository costCodeRepository;
    @Mock CostTransactionRepository costTransactionRepository;
    @Mock StockItemRepository stockItemRepository;
    @Mock StockMovementRepository stockMovementRepository;

    ProcurementService service;

    @BeforeEach
    void setUp() {
        service = new ProcurementService(requestRepository, quotationRepository, orderRepository,
                budgetRepository, supplierRepository, costCodeRepository, costTransactionRepository,
                stockItemRepository, stockMovementRepository);
    }

    @Test
    @DisplayName("should create quotation for a purchase request")
    void shouldCreateQuotation() {
        UUID prId = UUID.randomUUID();
        Budget budget = mock(Budget.class);
        PurchaseRequest pr = new PurchaseRequest(budget, null, "Cement", new BigDecimal("100"), "bags", "John");

        when(requestRepository.findById(prId)).thenReturn(Optional.of(pr));
        when(quotationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Quotation result = service.createQuotation(prId, LocalDate.of(2026, 2, 15));

        assertThat(result.getStatus()).isEqualTo("OPEN");
        assertThat(result.getDeadline()).isEqualTo(LocalDate.of(2026, 2, 15));
    }

    @Test
    @DisplayName("should analyze quotation and return best price first")
    void shouldAnalyzeAndReturnBestPrice() {
        UUID quotationId = UUID.randomUUID();
        Budget budget = mock(Budget.class);
        PurchaseRequest pr = new PurchaseRequest(budget, null, "Cement", new BigDecimal("100"), "bags", "John");
        Quotation quotation = new Quotation(pr, LocalDate.of(2026, 2, 15));

        Supplier s1 = mock(Supplier.class);
        when(s1.getName()).thenReturn("Supplier A");
        Supplier s2 = mock(Supplier.class);
        when(s2.getName()).thenReturn("Supplier B");

        quotation.getResponses().add(new QuotationResponse(quotation, s1, new BigDecimal("50.00"), 7, null));
        quotation.getResponses().add(new QuotationResponse(quotation, s2, new BigDecimal("42.50"), 10, null));

        when(quotationRepository.findById(quotationId)).thenReturn(Optional.of(quotation));

        ComparativeAnalysis analysis = service.analyze(quotationId);

        assertThat(analysis.quotes()).hasSize(2);
        assertThat(analysis.bestPrice().supplierName()).isEqualTo("Supplier B");
        assertThat(analysis.bestPrice().unitPrice()).isEqualByComparingTo("42.50");
    }

    @Test
    @DisplayName("should throw when generating order with no responses")
    void shouldThrowWhenNoResponses() {
        UUID quotationId = UUID.randomUUID();
        Budget budget = mock(Budget.class);
        PurchaseRequest pr = new PurchaseRequest(budget, null, "Cement", new BigDecimal("100"), "bags", "John");
        Quotation quotation = new Quotation(pr, null);

        when(quotationRepository.findById(quotationId)).thenReturn(Optional.of(quotation));

        assertThatThrownBy(() -> service.generateOrder(quotationId, "PO-001"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("No supplier responses");
    }

    @Test
    @DisplayName("should mark order as fully received when quantity met")
    void shouldMarkFullyReceived() {
        UUID orderId = UUID.randomUUID();
        Budget budget = mock(Budget.class);
        Supplier supplier = mock(Supplier.class);
        PurchaseOrder order = new PurchaseOrder(budget, supplier, null, "PO-001", "Cement",
                new BigDecimal("100"), new BigDecimal("42.50"), null);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(stockItemRepository.findByBudgetIdAndDescription(any(), any())).thenReturn(Optional.empty());
        when(stockItemRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(stockMovementRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Receiving r = service.receive(orderId, new BigDecimal("100"), LocalDate.of(2026, 3, 1), "All received");

        assertThat(r.getQuantityReceived()).isEqualByComparingTo("100");
        assertThat(order.getStatus()).isEqualTo("RECEIVED");
    }

    @Test
    @DisplayName("should mark order as partially received")
    void shouldMarkPartiallyReceived() {
        UUID orderId = UUID.randomUUID();
        Budget budget = mock(Budget.class);
        Supplier supplier = mock(Supplier.class);
        PurchaseOrder order = new PurchaseOrder(budget, supplier, null, "PO-001", "Cement",
                new BigDecimal("100"), new BigDecimal("42.50"), null);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(stockItemRepository.findByBudgetIdAndDescription(any(), any())).thenReturn(Optional.empty());
        when(stockItemRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(stockMovementRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.receive(orderId, new BigDecimal("60"), LocalDate.of(2026, 3, 1), "Partial");

        assertThat(order.getStatus()).isEqualTo("PARTIAL");
    }

    @Test
    @DisplayName("should list quotations filtered by purchase order response id")
    void shouldListQuotationsByOrder() {
        UUID budgetId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        UUID responseId = UUID.randomUUID();
        var pageable = PageRequest.of(0, 20);
        Budget budget = mock(Budget.class);
        when(budget.getId()).thenReturn(budgetId);
        Supplier supplier = mock(Supplier.class);
        PurchaseOrder order = new PurchaseOrder(budget, supplier, responseId, "PO-001", "Cement",
                new BigDecimal("100"), new BigDecimal("42.50"), null);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(quotationRepository.findByBudgetIdAndResponseId(budgetId, responseId, pageable))
                .thenReturn(new PageImpl<>(List.of(), pageable, 0));

        var result = service.listQuotationsByOrderPaged(budgetId, orderId, pageable);

        assertThat(result.getTotalElements()).isZero();
        verify(quotationRepository).findByBudgetIdAndResponseId(budgetId, responseId, pageable);
    }
}
