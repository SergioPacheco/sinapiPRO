package com.sinapipro.api.finance.application;

import module java.base;

import com.sinapipro.api.finance.domain.*;
import com.sinapipro.api.shared.error.DomainNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class FinanceService {

    private final PayableRepository payableRepository;
    private final ReceivableRepository receivableRepository;

    public FinanceService(PayableRepository payableRepository, ReceivableRepository receivableRepository) {
        this.payableRepository = payableRepository;
        this.receivableRepository = receivableRepository;
    }

    // --- Payables ---

    @Transactional
    public Payable createPayable(UUID budgetId, UUID supplierId, String description, BigDecimal amount,
                                 LocalDate dueDate, String category, UUID purchaseOrderId, UUID measurementId, String notes) {
        var payable = new Payable(budgetId, supplierId, description, amount, dueDate, category);
        payable.setPurchaseOrderId(purchaseOrderId);
        payable.setMeasurementId(measurementId);
        payable.setNotes(notes);
        return payableRepository.save(payable);
    }

    @Transactional
    public Payable payPayable(UUID payableId, BigDecimal paidAmount, LocalDate paidDate) {
        var payable = payableRepository.findById(payableId)
                .orElseThrow(() -> new DomainNotFoundException("Payable not found: " + payableId));
        payable.pay(paidAmount, paidDate);
        return payableRepository.save(payable);
    }

    @Transactional
    public Payable cancelPayable(UUID payableId) {
        var payable = payableRepository.findById(payableId)
                .orElseThrow(() -> new DomainNotFoundException("Payable not found: " + payableId));
        payable.cancel();
        return payableRepository.save(payable);
    }

    public Page<Payable> listPayables(UUID budgetId, Pageable pageable) {
        return payableRepository.findByBudgetId(budgetId, pageable);
    }

    public List<Payable> overduePayables(UUID budgetId) {
        return payableRepository.findOverdue(budgetId, LocalDate.now());
    }

    // --- Receivables ---

    @Transactional
    public Receivable createReceivable(UUID budgetId, String description, BigDecimal amount,
                                       LocalDate dueDate, String category, UUID measurementId, UUID invoiceId, String notes) {
        var receivable = new Receivable(budgetId, description, amount, dueDate, category);
        receivable.setMeasurementId(measurementId);
        receivable.setInvoiceId(invoiceId);
        receivable.setNotes(notes);
        return receivableRepository.save(receivable);
    }

    @Transactional
    public Receivable receivePayment(UUID receivableId, BigDecimal receivedAmount, LocalDate receivedDate) {
        var receivable = receivableRepository.findById(receivableId)
                .orElseThrow(() -> new DomainNotFoundException("Receivable not found: " + receivableId));
        receivable.receive(receivedAmount, receivedDate);
        return receivableRepository.save(receivable);
    }

    @Transactional
    public Receivable cancelReceivable(UUID receivableId) {
        var receivable = receivableRepository.findById(receivableId)
                .orElseThrow(() -> new DomainNotFoundException("Receivable not found: " + receivableId));
        receivable.cancel();
        return receivableRepository.save(receivable);
    }

    public Page<Receivable> listReceivables(UUID budgetId, Pageable pageable) {
        return receivableRepository.findByBudgetId(budgetId, pageable);
    }

    public List<Receivable> overdueReceivables(UUID budgetId) {
        return receivableRepository.findOverdue(budgetId, LocalDate.now());
    }

    // --- Consolidated Cash Flow (multi-project) ---

    public ConsolidatedCashFlow consolidatedCashFlow(List<UUID> projectIds) {
        var projects = projectIds.stream()
                .map(id -> new ProjectCashFlow(id, cashFlowSummary(id)))
                .toList();

        var totalPayablesPending = projects.stream().map(p -> p.summary().payablesPending()).reduce(BigDecimal.ZERO, BigDecimal::add);
        var totalPayablesPaid = projects.stream().map(p -> p.summary().payablesPaid()).reduce(BigDecimal.ZERO, BigDecimal::add);
        var totalReceivablesPending = projects.stream().map(p -> p.summary().receivablesPending()).reduce(BigDecimal.ZERO, BigDecimal::add);
        var totalReceivablesReceived = projects.stream().map(p -> p.summary().receivablesReceived()).reduce(BigDecimal.ZERO, BigDecimal::add);
        var balance = totalReceivablesReceived.subtract(totalPayablesPaid);
        var projected = totalReceivablesPending.subtract(totalPayablesPending);

        return new ConsolidatedCashFlow(projects,
                new CashFlowSummary(totalPayablesPending, totalPayablesPaid, totalReceivablesPending, totalReceivablesReceived, balance, projected));
    }

    // --- Cash Flow ---

    public CashFlowSummary cashFlowSummary(UUID budgetId) {
        var totalPayablesPending = payableRepository.sumPendingByBudget(budgetId);
        var totalPayablesPaid = payableRepository.sumPaidByBudget(budgetId);
        var totalReceivablesPending = receivableRepository.sumPendingByBudget(budgetId);
        var totalReceivablesReceived = receivableRepository.sumReceivedByBudget(budgetId);
        var balance = totalReceivablesReceived.subtract(totalPayablesPaid);
        var projectedBalance = totalReceivablesPending.subtract(totalPayablesPending);
        return new CashFlowSummary(totalPayablesPending, totalPayablesPaid, totalReceivablesPending,
                totalReceivablesReceived, balance, projectedBalance);
    }

    public CashFlowProjection cashFlowProjection(UUID budgetId, LocalDate startDate, LocalDate endDate) {
        var payables = payableRepository.findByBudgetIdAndDueDateBetween(budgetId, startDate, endDate);
        var receivables = receivableRepository.findByBudgetIdAndDueDateBetween(budgetId, startDate, endDate);

        // Group by month
        Map<String, BigDecimal> monthlyInflows = new TreeMap<>();
        Map<String, BigDecimal> monthlyOutflows = new TreeMap<>();

        receivables.forEach(r -> {
            String month = r.getDueDate().withDayOfMonth(1).toString();
            monthlyInflows.merge(month, r.getAmount(), BigDecimal::add);
        });

        payables.forEach(p -> {
            String month = p.getDueDate().withDayOfMonth(1).toString();
            monthlyOutflows.merge(month, p.getAmount(), BigDecimal::add);
        });

        Set<String> allMonths = new TreeSet<>();
        allMonths.addAll(monthlyInflows.keySet());
        allMonths.addAll(monthlyOutflows.keySet());

        List<CashFlowMonth> months = new ArrayList<>();
        BigDecimal cumulative = BigDecimal.ZERO;
        for (String month : allMonths) {
            var inflow = monthlyInflows.getOrDefault(month, BigDecimal.ZERO);
            var outflow = monthlyOutflows.getOrDefault(month, BigDecimal.ZERO);
            var net = inflow.subtract(outflow);
            cumulative = cumulative.add(net);
            months.add(new CashFlowMonth(month, inflow, outflow, net, cumulative));
        }

        return new CashFlowProjection(startDate, endDate, months);
    }

    // --- Records ---
    public record CashFlowSummary(BigDecimal payablesPending, BigDecimal payablesPaid,
                                   BigDecimal receivablesPending, BigDecimal receivablesReceived,
                                   BigDecimal currentBalance, BigDecimal projectedBalance) {}

    public record CashFlowProjection(LocalDate startDate, LocalDate endDate, List<CashFlowMonth> months) {}

    public record CashFlowMonth(String month, BigDecimal inflows, BigDecimal outflows,
                                BigDecimal netFlow, BigDecimal cumulativeBalance) {}

    public record ConsolidatedCashFlow(List<ProjectCashFlow> projects, CashFlowSummary consolidated) {}
    public record ProjectCashFlow(UUID projectId, CashFlowSummary summary) {}
}
