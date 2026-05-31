package com.sinapipro.api.invoice;

import com.sinapipro.api.budget.domain.Budget;
import com.sinapipro.api.budget.domain.BudgetRepository;
import com.sinapipro.api.invoice.api.CreateInvoiceRequest;
import com.sinapipro.api.invoice.application.InvoiceService;
import com.sinapipro.api.invoice.domain.Invoice;
import com.sinapipro.api.invoice.domain.InvoiceRepository;
import com.sinapipro.api.invoice.domain.InvoiceStatus;
import com.sinapipro.api.shared.error.DomainNotFoundException;
import com.sinapipro.api.shared.events.OperationEventPublisher;
import com.sinapipro.api.shared.observability.BusinessMetricsService;
import com.sinapipro.api.shared.observability.BusinessObservationService;
import com.sinapipro.api.supplier.domain.Supplier;
import com.sinapipro.api.supplier.domain.SupplierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class InvoiceServiceTest {

    private InvoiceRepository invoiceRepo;
    private BudgetRepository budgetRepo;
    private SupplierRepository supplierRepo;
    private OperationEventPublisher eventPublisher;
    private BusinessMetricsService metricsService;
    private BusinessObservationService observationService;
    private InvoiceService service;

    @BeforeEach
    void setUp() {
        invoiceRepo = Mockito.mock(InvoiceRepository.class);
        budgetRepo = Mockito.mock(BudgetRepository.class);
        supplierRepo = Mockito.mock(SupplierRepository.class);
        eventPublisher = Mockito.mock(OperationEventPublisher.class);
        metricsService = Mockito.mock(BusinessMetricsService.class);
        observationService = Mockito.mock(BusinessObservationService.class);
        // Make observationService.observe() just execute the supplier
        when(observationService.observe(anyString(), anyString(), any(java.util.function.Supplier.class)))
                .thenAnswer(inv -> ((java.util.function.Supplier<?>) inv.getArgument(2)).get());
        service = new InvoiceService(invoiceRepo, budgetRepo, supplierRepo, eventPublisher, metricsService, observationService);
    }

    @Test
    @DisplayName("should create invoice successfully")
    void shouldCreateInvoice() {
        UUID budgetId = UUID.randomUUID();
        UUID supplierId = UUID.randomUUID();
        var budget = Mockito.mock(Budget.class);
        var supplier = Mockito.mock(com.sinapipro.api.supplier.domain.Supplier.class);

        when(invoiceRepo.existsByNumber("NF-001")).thenReturn(false);
        when(budgetRepo.findById(budgetId)).thenReturn(Optional.of(budget));
        when(supplierRepo.findById(supplierId)).thenReturn(Optional.of(supplier));
        when(invoiceRepo.save(any(Invoice.class))).thenAnswer(inv -> {
            var saved = Mockito.mock(Invoice.class);
            when(saved.getId()).thenReturn(UUID.randomUUID());
            when(saved.getNumber()).thenReturn("NF-001");
            when(saved.getAmount()).thenReturn(new BigDecimal("15000.00"));
            return saved;
        });

        var request = new CreateInvoiceRequest("NF-001", budgetId, supplierId,
                new BigDecimal("15000.00"), LocalDate.of(2026, 5, 1), LocalDate.of(2026, 6, 1),
                InvoiceStatus.PENDING, "Test invoice");

        var invoice = service.create(request);

        assertThat(invoice.getNumber()).isEqualTo("NF-001");
        assertThat(invoice.getAmount()).isEqualByComparingTo("15000.00");
        verify(invoiceRepo).save(any());
    }

    @Test
    @DisplayName("should reject duplicate invoice number")
    void shouldRejectDuplicateNumber() {
        when(invoiceRepo.existsByNumber("NF-001")).thenReturn(true);

        var request = new CreateInvoiceRequest("NF-001", UUID.randomUUID(), UUID.randomUUID(),
                BigDecimal.TEN, LocalDate.now(), LocalDate.now().plusDays(30), InvoiceStatus.PENDING, null);

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    @DisplayName("should throw when budget not found")
    void shouldThrowWhenBudgetNotFound() {
        UUID budgetId = UUID.randomUUID();
        when(invoiceRepo.existsByNumber("NF-002")).thenReturn(false);
        when(budgetRepo.findById(budgetId)).thenReturn(Optional.empty());

        var request = new CreateInvoiceRequest("NF-002", budgetId, UUID.randomUUID(),
                BigDecimal.TEN, LocalDate.now(), LocalDate.now().plusDays(30), InvoiceStatus.PENDING, null);

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(DomainNotFoundException.class);
    }

    @Test
    @DisplayName("should find invoice by id")
    void shouldFindById() {
        UUID id = UUID.randomUUID();
        var invoice = Mockito.mock(Invoice.class);
        when(invoiceRepo.findById(id)).thenReturn(Optional.of(invoice));

        assertThat(service.findById(id)).isEqualTo(invoice);
    }

    @Test
    @DisplayName("should throw when invoice not found")
    void shouldThrowWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(invoiceRepo.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(id))
                .isInstanceOf(DomainNotFoundException.class);
    }
}
