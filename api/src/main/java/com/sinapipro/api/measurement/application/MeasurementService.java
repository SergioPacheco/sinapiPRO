package com.sinapipro.api.measurement.application;

import com.sinapipro.api.budget.domain.Budget;
import com.sinapipro.api.budget.domain.BudgetRepository;
import com.sinapipro.api.invoice.domain.Invoice;
import com.sinapipro.api.invoice.domain.InvoiceRepository;
import com.sinapipro.api.invoice.domain.InvoiceStatus;
import com.sinapipro.api.jobcosting.domain.CostCode;
import com.sinapipro.api.jobcosting.domain.CostCodeRepository;
import com.sinapipro.api.jobcosting.domain.CostTransaction;
import com.sinapipro.api.jobcosting.domain.CostTransactionRepository;
import com.sinapipro.api.jobcosting.domain.CostTransactionType;
import com.sinapipro.api.measurement.domain.*;
import com.sinapipro.api.shared.error.DomainNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class MeasurementService {

    private final MeasurementRepository measurementRepository;
    private final BudgetRepository budgetRepository;
    private final CostCodeRepository costCodeRepository;
    private final CostTransactionRepository costTransactionRepository;
    private final InvoiceRepository invoiceRepository;

    public MeasurementService(MeasurementRepository measurementRepository, BudgetRepository budgetRepository,
                              CostCodeRepository costCodeRepository, CostTransactionRepository costTransactionRepository,
                              InvoiceRepository invoiceRepository) {
        this.measurementRepository = measurementRepository;
        this.budgetRepository = budgetRepository;
        this.costCodeRepository = costCodeRepository;
        this.costTransactionRepository = costTransactionRepository;
        this.invoiceRepository = invoiceRepository;
    }

    @Transactional
    public Measurement create(UUID budgetId, Integer number, LocalDate periodStart, LocalDate periodEnd,
                              BigDecimal retentionPct, List<ItemInput> items) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new DomainNotFoundException("Budget not found: " + budgetId));
        Measurement m = new Measurement(budget, number, periodStart, periodEnd, retentionPct);
        for (var item : items) {
            m.getItems().add(new MeasurementItem(m, item.costCodeId(), item.description(), item.quantity(), item.unitPrice()));
        }
        return measurementRepository.save(m);
    }

    @Transactional
    public Measurement submit(UUID measurementId) {
        Measurement m = findOrThrow(measurementId);
        m.submit();
        return measurementRepository.save(m);
    }

    @Transactional
    public Measurement approve(UUID measurementId) {
        Measurement m = findOrThrow(measurementId);
        m.approve();
        Measurement saved = measurementRepository.save(m);

        // Generate ACTUAL cost transactions for each item with a cost code (idempotent)
        for (MeasurementItem item : saved.getItems()) {
            if (item.getCostCodeId() != null) {
                costCodeRepository.findById(item.getCostCodeId()).ifPresent(costCode -> {
                    if (!costTransactionRepository.existsByReferenceIdAndType(saved.getId(), CostTransactionType.ACTUAL)) {
                        CostTransaction tx = new CostTransaction(costCode, CostTransactionType.ACTUAL,
                                item.getAmount().setScale(2, java.math.RoundingMode.HALF_UP),
                                "Measurement #" + saved.getNumber() + ": " + item.getDescription(),
                                saved.getId(), saved.getPeriodEnd());
                        costTransactionRepository.save(tx);
                    }
                });
            }
        }

        // Progress Billing: auto-generate invoice from approved measurement
        generateInvoiceFromMeasurement(saved);

        return saved;
    }

    private void generateInvoiceFromMeasurement(Measurement measurement) {
        String invoiceNumber = "MED-" + measurement.getBudget().getId().toString().substring(0, 8)
                + "-" + measurement.getNumber();
        if (invoiceRepository.existsByNumber(invoiceNumber)) return;

        Invoice invoice = new Invoice(invoiceNumber, measurement.getBudget(), null,
                measurement.getNetAmount().setScale(2, java.math.RoundingMode.HALF_UP),
                measurement.getPeriodEnd(), measurement.getPeriodEnd().plusDays(30),
                InvoiceStatus.PENDING,
                "Auto-generated from Measurement #" + measurement.getNumber());
        invoiceRepository.save(invoice);
    }

    public MeasurementSummary summary(UUID budgetId) {
        List<Measurement> all = measurementRepository.findByBudgetIdOrderByNumberDesc(budgetId);

        BigDecimal totalGross = BigDecimal.ZERO;
        BigDecimal totalNet = BigDecimal.ZERO;
        BigDecimal totalRetention = BigDecimal.ZERO;

        for (Measurement m : all) {
            if (m.getStatus() == MeasurementStatus.APPROVED || m.getStatus() == MeasurementStatus.PAID) {
                BigDecimal gross = m.getGrossAmount();
                BigDecimal net = m.getNetAmount();
                totalGross = totalGross.add(gross);
                totalNet = totalNet.add(net);
                totalRetention = totalRetention.add(gross.subtract(net));
            }
        }

        return new MeasurementSummary(all.size(), totalGross, totalNet, totalRetention);
    }

    public CumulativeResult cumulative(UUID budgetId, UUID measurementId) {
        List<Measurement> all = measurementRepository.findByBudgetIdOrderByNumberDesc(budgetId);
        Measurement current = findOrThrow(measurementId);

        BigDecimal previousCumulative = BigDecimal.ZERO;
        for (Measurement m : all) {
            if (m.getNumber() < current.getNumber()
                    && (m.getStatus() == MeasurementStatus.APPROVED || m.getStatus() == MeasurementStatus.PAID)) {
                previousCumulative = previousCumulative.add(m.getGrossAmount());
            }
        }

        BigDecimal currentGross = current.getGrossAmount();
        BigDecimal totalCumulative = previousCumulative.add(currentGross);

        return new CumulativeResult(previousCumulative, currentGross, totalCumulative);
    }

    public BalanceResult balance(UUID budgetId, BigDecimal contractedTotal) {
        List<Measurement> all = measurementRepository.findByBudgetIdOrderByNumberDesc(budgetId);

        BigDecimal measured = BigDecimal.ZERO;
        for (Measurement m : all) {
            if (m.getStatus() == MeasurementStatus.APPROVED || m.getStatus() == MeasurementStatus.PAID) {
                measured = measured.add(m.getGrossAmount());
            }
        }

        BigDecimal remaining = contractedTotal.subtract(measured);
        BigDecimal percentMeasured = contractedTotal.compareTo(BigDecimal.ZERO) > 0
                ? measured.multiply(BigDecimal.valueOf(100)).divide(contractedTotal, 2, java.math.RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return new BalanceResult(contractedTotal, measured, remaining, percentMeasured);
    }

    private Measurement findOrThrow(UUID id) {
        return measurementRepository.findById(id)
                .orElseThrow(() -> new DomainNotFoundException("Measurement not found: " + id));
    }

    public record ItemInput(UUID costCodeId, String description, BigDecimal quantity, BigDecimal unitPrice) {}
    public record MeasurementSummary(int totalMeasurements, BigDecimal totalGross, BigDecimal totalNet, BigDecimal totalRetention) {}
    public record CumulativeResult(BigDecimal previousCumulative, BigDecimal currentPeriod, BigDecimal totalCumulative) {}
    public record BalanceResult(BigDecimal contractedTotal, BigDecimal measured, BigDecimal remaining, BigDecimal percentMeasured) {}
}
