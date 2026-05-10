package com.sinapipro.api.analytics.application;

import module java.base;

import com.sinapipro.api.invoice.domain.Invoice;
import com.sinapipro.api.invoice.domain.InvoiceRepository;
import com.sinapipro.api.invoice.domain.InvoiceStatus;
import com.sinapipro.api.measurement.domain.Measurement;
import com.sinapipro.api.measurement.domain.MeasurementRepository;
import com.sinapipro.api.measurement.domain.MeasurementStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CashFlowService {

    private final MeasurementRepository measurementRepository;
    private final InvoiceRepository invoiceRepository;

    public CashFlowService(MeasurementRepository measurementRepository, InvoiceRepository invoiceRepository) {
        this.measurementRepository = measurementRepository;
        this.invoiceRepository = invoiceRepository;
    }

    public CashFlowProjection project(UUID budgetId) {
        var measurements = measurementRepository.findByBudgetIdOrderByNumberDesc(budgetId);
        var incomeByMonth = new TreeMap<YearMonth, BigDecimal>();

        for (var m : measurements) {
            if (m.getStatus() == MeasurementStatus.APPROVED || m.getStatus() == MeasurementStatus.PAID) {
                var month = YearMonth.from(m.getPeriodEnd());
                incomeByMonth.merge(month, m.getNetAmount(), BigDecimal::add);
            }
        }

        var invoices = invoiceRepository.findFiltered(null, budgetId, null, Pageable.unpaged()).getContent();
        var expenseByMonth = new TreeMap<YearMonth, BigDecimal>();

        for (var inv : invoices) {
            if (inv.getStatus() != InvoiceStatus.CANCELLED && !inv.getNumber().startsWith("MED-")) {
                var month = YearMonth.from(inv.getDueDate());
                expenseByMonth.merge(month, inv.getAmount(), BigDecimal::add);
            }
        }

        var allMonths = new TreeSet<YearMonth>();
        allMonths.addAll(incomeByMonth.keySet());
        allMonths.addAll(expenseByMonth.keySet());

        var months = new ArrayList<MonthlyFlow>();
        var cumulativeBalance = BigDecimal.ZERO;

        for (var month : allMonths) {
            var income = incomeByMonth.getOrDefault(month, BigDecimal.ZERO);
            var expense = expenseByMonth.getOrDefault(month, BigDecimal.ZERO);
            var netFlow = income.subtract(expense);
            cumulativeBalance = cumulativeBalance.add(netFlow);
            months.add(new MonthlyFlow(month.toString(), income, expense, netFlow, cumulativeBalance));
        }

        var totalIncome = incomeByMonth.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        var totalExpense = expenseByMonth.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CashFlowProjection(months, totalIncome, totalExpense, totalIncome.subtract(totalExpense));
    }

    public record CashFlowProjection(List<MonthlyFlow> months, BigDecimal totalIncome,
                                     BigDecimal totalExpense, BigDecimal netBalance) {}

    public record MonthlyFlow(String month, BigDecimal income, BigDecimal expense,
                              BigDecimal netFlow, BigDecimal cumulativeBalance) {}

    public Map<String, Object> consolidated() {
        // Placeholder — in production, aggregate cash flow across all projects
        return Map.of("totalIncome", BigDecimal.ZERO, "totalExpense", BigDecimal.ZERO, "netBalance", BigDecimal.ZERO);
    }
}
