package com.sinapipro.api.analytics.application;

import com.sinapipro.api.budget.domain.BudgetRepository;
import com.sinapipro.api.invoice.domain.InvoiceRepository;
import com.sinapipro.api.supplier.domain.SupplierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional(readOnly = true)
public class PortfolioAnalyticsService {

    private final BudgetRepository budgetRepository;
    private final SupplierRepository supplierRepository;
    private final InvoiceRepository invoiceRepository;

    public PortfolioAnalyticsService(BudgetRepository budgetRepository, SupplierRepository supplierRepository,
                                     InvoiceRepository invoiceRepository) {
        this.budgetRepository = budgetRepository;
        this.supplierRepository = supplierRepository;
        this.invoiceRepository = invoiceRepository;
    }

    public PortfolioSummary summary() {
        long totalBudgets = budgetRepository.count();
        long activeSuppliers = supplierRepository.count();
        long totalInvoices = invoiceRepository.count();
        return new PortfolioSummary(totalBudgets, activeSuppliers, totalInvoices);
    }

    public record PortfolioSummary(long totalBudgets, long activeSuppliers, long totalInvoices) {}
}
