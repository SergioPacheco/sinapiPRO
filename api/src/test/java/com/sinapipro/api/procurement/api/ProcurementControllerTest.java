package com.sinapipro.api.procurement.api;

import com.sinapipro.api.budget.domain.Budget;
import com.sinapipro.api.budget.domain.BudgetRepository;
import com.sinapipro.api.procurement.application.ProcurementReportService;
import com.sinapipro.api.procurement.application.ProcurementService;
import com.sinapipro.api.procurement.application.QuotationEmailService;
import com.sinapipro.api.procurement.domain.*;
import com.sinapipro.api.shared.error.DomainNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcurementControllerTest {

    @Mock PurchaseRequestRepository requestRepository;
    @Mock BudgetRepository budgetRepository;
    @Mock ProcurementService procurementService;
    @Mock ProcurementReportService procurementReportService;
    @Mock PurchaseOrderCostDistributionRepository costDistributionRepository;
    @Mock PurchaseOrderRepository orderRepository;
    @Mock QuotationRepository quotationRepository;
    @Mock QuotationEmailRepository quotationEmailRepository;
    @Mock QuotationEmailService quotationEmailService;

    ProcurementController controller;

    @BeforeEach
    void setUp() {
        controller = new ProcurementController(
                requestRepository, budgetRepository, procurementService, procurementReportService,
                costDistributionRepository, orderRepository, quotationRepository, quotationEmailRepository,
                quotationEmailService
        );
    }

    @Test
    @DisplayName("should reject quotation access when it belongs to another project")
    void shouldRejectQuotationFromAnotherProject() {
        UUID projectId = UUID.randomUUID();
        UUID anotherProjectId = UUID.randomUUID();
        UUID quotationId = UUID.randomUUID();

        Budget budget = mock(Budget.class);
        when(budget.getId()).thenReturn(anotherProjectId);
        PurchaseRequest request = mock(PurchaseRequest.class);
        when(request.getBudget()).thenReturn(budget);
        Quotation quotation = mock(Quotation.class);
        when(quotation.getPurchaseRequest()).thenReturn(request);
        when(quotationRepository.findById(quotationId)).thenReturn(Optional.of(quotation));

        assertThatThrownBy(() -> controller.analyze(projectId, quotationId))
                .isInstanceOf(DomainNotFoundException.class)
                .hasMessageContaining("Quotation not found in project");

        verify(procurementService, never()).analyze(any());
    }

    @Test
    @DisplayName("should reject order receiving when order belongs to another project")
    void shouldRejectOrderFromAnotherProject() {
        UUID projectId = UUID.randomUUID();
        UUID anotherProjectId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();

        Budget budget = mock(Budget.class);
        when(budget.getId()).thenReturn(anotherProjectId);
        PurchaseOrder order = mock(PurchaseOrder.class);
        when(order.getBudget()).thenReturn(budget);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        var req = new ProcurementController.ReceiveReq(new java.math.BigDecimal("1"), java.time.LocalDate.now(), "ok");
        assertThatThrownBy(() -> controller.receive(projectId, orderId, req))
                .isInstanceOf(DomainNotFoundException.class)
                .hasMessageContaining("Order not found in project");

        verify(procurementService, never()).receive(any(), any(), any(), any());
    }
}
