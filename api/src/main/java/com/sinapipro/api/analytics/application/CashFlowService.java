package com.sinapipro.api.analytics.application;

import com.sinapipro.api.invoice.domain.Invoice;
import com.sinapipro.api.invoice.domain.InvoiceRepository;
import com.sinapipro.api.invoice.domain.InvoiceStatus;
import com.sinapipro.api.measurement.domain.Measurement;
import com.sinapipro.api.measurement.domain.MeasurementRepository;
import com.sinapipro.api.measurement.domain.MeasurementStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

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
        // Income: approved measurements (net amount, by period end month)
        List<Measurement> measurements = measurementRepository.findByBudgetIdOrderByNumberDesc(budgetId);
        Map<YearMonth, BigDecimal> incomeByMonth = new TreeMap<>();

        for (Measurement m : measurements) {
            if (m.getStatus() == MeasurementStatus.APPROVED || m.getStatus() == MeasurementStatus.PAID) {
                YearMonth month = YearMonth.from(m.getPeriodEnd());
                incomeByMonth.merge(month, m.getNetAmount(), BigDecimal::add);
            }
        }

        // Expenses: invoices for this budget (by due date month) — exclude auto-generated measurement invoices
        List<Invoice> invoices = invoiceRepository.findFiltered(null, budgetId, null, Pageable.unpaged()).getContent();
        Map<YearMonth, BigDecimal> expenseByMonth = new TreeMap<>();

        for (Invoice inv : invoices) {
            if (inv.getStatus() != InvoiceStatus.CANCELLED && !inv.getNumber().startsWith("MED-")) {
                YearMonth month = YearMonth.from(inv.getDueDate());
                expenseByMonth.merge(month, inv.getAmount(), BigDecimal::add);
            }
        }

        // Build monthly projection
        Set<YearMonth> allMonths = new TreeSet<>();
        allMonths.addAll(incomeByMonth.keySet());
        allMonths.addAll(expenseByMonth.keySet());

        List<MonthlyFlow> months = new ArrayList<>();
        BigDecimal cumulativeBalance = BigDecimal.ZERO;

        for (YearMonth month : allMonths) {
            BigDecimal income = incomeByMonth.getOrDefault(month, BigDecimal.ZERO);
            BigDecimal expense = expenseByMonth.getOrDefault(month, BigDecimal.ZERO);
            BigDecimal netFlow = income.subtract(expense);
            cumulativeBalance = cumulativeBalance.add(netFlow);
            months.add(new MonthlyFlow(month.toString(), income, expense, netFlow, cumulativeBalance));
        }

        BigDecimal totalIncome = incomeByMonth.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalExpense = expenseByMonth.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CashFlowProjection(months, totalIncome, totalExpense, totalIncome.subtract(totalExpense));
    }

    public record CashFlowProjection(List<MonthlyFlow> months, BigDecimal totalIncome,
                                     BigDecimal totalExpense, BigDecimal netBalance) {}

    public record MonthlyFlow(String month, BigDecimal income, BigDecimal expense,
                              BigDecimal netFlow, BigDecimal cumulativeBalance) {}
}
