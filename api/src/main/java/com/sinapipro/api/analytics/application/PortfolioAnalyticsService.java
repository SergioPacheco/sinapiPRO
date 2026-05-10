package com.sinapipro.api.analytics.application;

import module java.base;

import com.sinapipro.api.budget.domain.BudgetRepository;
import com.sinapipro.api.invoice.domain.InvoiceRepository;
import com.sinapipro.api.supplier.domain.SupplierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        var totalBudgets = budgetRepository.count();
        var activeSuppliers = supplierRepository.count();
        var totalInvoices = invoiceRepository.count();
        return new PortfolioSummary(totalBudgets, activeSuppliers, totalInvoices);
    }

    public record PortfolioSummary(long totalBudgets, long activeSuppliers, long totalInvoices) {}

    public List<Map<String, Object>> projectsAtRisk() {
        // Placeholder — in production, query projects where CPI < 1 or SPI < 1
        return List.of();
    }

    public List<Map<String, Object>> contractsExpiring() {
        // Placeholder — in production, query contracts ending within 30 days
        return List.of();
    }

    public List<Map<String, Object>> pendingMeasurements() {
        // Placeholder — in production, query measurements with status SUBMITTED
        return List.of();
    }
}
