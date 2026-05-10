package com.sinapipro.api.analytics;

import com.sinapipro.api.analytics.application.CashFlowService;
import com.sinapipro.api.analytics.application.CashFlowService.CashFlowProjection;
import com.sinapipro.api.budget.domain.Budget;
import com.sinapipro.api.invoice.domain.Invoice;
import com.sinapipro.api.invoice.domain.InvoiceRepository;
import com.sinapipro.api.invoice.domain.InvoiceStatus;
import com.sinapipro.api.measurement.domain.Measurement;
import com.sinapipro.api.measurement.domain.MeasurementItem;
import com.sinapipro.api.measurement.domain.MeasurementRepository;
import com.sinapipro.api.supplier.domain.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CashFlowServiceTest {

    @Mock MeasurementRepository measurementRepository;
    @Mock InvoiceRepository invoiceRepository;

    CashFlowService service;

    @BeforeEach
    void setUp() {
        service = new CashFlowService(measurementRepository, invoiceRepository);
    }

    @Test
    @DisplayName("should project income from approved measurements")
    void shouldProjectIncome() {
        UUID budgetId = UUID.randomUUID();
        Budget budget = mock(Budget.class);

        Measurement m = new Measurement(budget, 1, LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31), BigDecimal.ZERO);
        m.submit();
        m.approve();
        m.getItems().add(new MeasurementItem(m, (UUID) null, "Work", new BigDecimal("1"), new BigDecimal("10000")));

        when(measurementRepository.findByBudgetIdOrderByNumberDesc(budgetId)).thenReturn(List.of(m));
        when(invoiceRepository.findFiltered(any(), eq(budgetId), any(), any())).thenReturn(new PageImpl<>(List.of()));

        CashFlowProjection result = service.project(budgetId);

        assertThat(result.totalIncome()).isEqualByComparingTo("10000");
        assertThat(result.totalExpense()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.netBalance()).isEqualByComparingTo("10000");
        assertThat(result.months()).hasSize(1);
        assertThat(result.months().getFirst().month()).isEqualTo("2026-01");
    }

    @Test
    @DisplayName("should project expenses from invoices")
    void shouldProjectExpenses() {
        UUID budgetId = UUID.randomUUID();
        Budget budget = mock(Budget.class);
        Supplier supplier = mock(Supplier.class);

        Invoice inv = new Invoice("NF-001", budget, supplier, new BigDecimal("5000"),
                LocalDate.of(2026, 2, 1), LocalDate.of(2026, 2, 28), InvoiceStatus.PENDING, null);

        when(measurementRepository.findByBudgetIdOrderByNumberDesc(budgetId)).thenReturn(List.of());
        when(invoiceRepository.findFiltered(any(), eq(budgetId), any(), any())).thenReturn(new PageImpl<>(List.of(inv)));

        CashFlowProjection result = service.project(budgetId);

        assertThat(result.totalExpense()).isEqualByComparingTo("5000");
        assertThat(result.netBalance()).isEqualByComparingTo("-5000");
    }

    @Test
    @DisplayName("should calculate cumulative balance across months")
    void shouldCalculateCumulativeBalance() {
        UUID budgetId = UUID.randomUUID();
        Budget budget = mock(Budget.class);
        Supplier supplier = mock(Supplier.class);

        // Income in Jan: 20000
        Measurement m = new Measurement(budget, 1, LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31), BigDecimal.ZERO);
        m.submit();
        m.approve();
        m.getItems().add(new MeasurementItem(m, (UUID) null, "Work", new BigDecimal("1"), new BigDecimal("20000")));

        // Expense in Feb: 8000
        Invoice inv = new Invoice("NF-001", budget, supplier, new BigDecimal("8000"),
                LocalDate.of(2026, 2, 1), LocalDate.of(2026, 2, 15), InvoiceStatus.PAID, null);

        when(measurementRepository.findByBudgetIdOrderByNumberDesc(budgetId)).thenReturn(List.of(m));
        when(invoiceRepository.findFiltered(any(), eq(budgetId), any(), any())).thenReturn(new PageImpl<>(List.of(inv)));

        CashFlowProjection result = service.project(budgetId);

        assertThat(result.months()).hasSize(2);
        // Jan: +20000, cumulative = 20000
        assertThat(result.months().get(0).cumulativeBalance()).isEqualByComparingTo("20000");
        // Feb: -8000, cumulative = 12000
        assertThat(result.months().get(1).cumulativeBalance()).isEqualByComparingTo("12000");
    }

    @Test
    @DisplayName("should exclude cancelled invoices")
    void shouldExcludeCancelledInvoices() {
        UUID budgetId = UUID.randomUUID();
        Budget budget = mock(Budget.class);
        Supplier supplier = mock(Supplier.class);

        Invoice cancelled = new Invoice("NF-X", budget, supplier, new BigDecimal("99999"),
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31), InvoiceStatus.CANCELLED, null);

        when(measurementRepository.findByBudgetIdOrderByNumberDesc(budgetId)).thenReturn(List.of());
        when(invoiceRepository.findFiltered(any(), eq(budgetId), any(), any())).thenReturn(new PageImpl<>(List.of(cancelled)));

        CashFlowProjection result = service.project(budgetId);

        assertThat(result.totalExpense()).isEqualByComparingTo(BigDecimal.ZERO);
    }
}
