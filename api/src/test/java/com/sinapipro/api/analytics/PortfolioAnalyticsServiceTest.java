package com.sinapipro.api.analytics;

import com.sinapipro.api.analytics.application.PortfolioAnalyticsService;
import com.sinapipro.api.analytics.application.PortfolioAnalyticsService.PortfolioSummary;
import com.sinapipro.api.budget.domain.BudgetRepository;
import com.sinapipro.api.invoice.domain.InvoiceRepository;
import com.sinapipro.api.supplier.domain.SupplierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PortfolioAnalyticsServiceTest {

    @Mock BudgetRepository budgetRepository;
    @Mock SupplierRepository supplierRepository;
    @Mock InvoiceRepository invoiceRepository;

    PortfolioAnalyticsService service;

    @BeforeEach
    void setUp() {
        service = new PortfolioAnalyticsService(budgetRepository, supplierRepository, invoiceRepository);
    }

    @Test
    @DisplayName("should aggregate counts from all repositories")
    void shouldAggregateCounts() {
        when(budgetRepository.count()).thenReturn(5L);
        when(supplierRepository.count()).thenReturn(12L);
        when(invoiceRepository.count()).thenReturn(34L);

        PortfolioSummary result = service.summary();

        assertThat(result.totalBudgets()).isEqualTo(5);
        assertThat(result.activeSuppliers()).isEqualTo(12);
        assertThat(result.totalInvoices()).isEqualTo(34);
    }

    @Test
    @DisplayName("should return zeros when no data")
    void shouldReturnZerosWhenEmpty() {
        when(budgetRepository.count()).thenReturn(0L);
        when(supplierRepository.count()).thenReturn(0L);
        when(invoiceRepository.count()).thenReturn(0L);

        PortfolioSummary result = service.summary();

        assertThat(result.totalBudgets()).isZero();
        assertThat(result.activeSuppliers()).isZero();
        assertThat(result.totalInvoices()).isZero();
    }
}
