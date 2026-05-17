package com.sinapipro.api.measurement.application;

import module java.base;

import java.util.stream.Gatherers;

import com.sinapipro.api.budget.domain.Budget;
import com.sinapipro.api.budget.domain.BudgetItem;
import com.sinapipro.api.budget.domain.BudgetItemRepository;
import com.sinapipro.api.budget.domain.BudgetRepository;
import com.sinapipro.api.shared.error.DomainValidationException;
import com.sinapipro.api.invoice.domain.Invoice;
import com.sinapipro.api.invoice.domain.InvoiceRepository;
import com.sinapipro.api.invoice.domain.InvoiceStatus;
import com.sinapipro.api.finance.domain.Receivable;
import com.sinapipro.api.finance.domain.ReceivableRepository;
import com.sinapipro.api.jobcosting.domain.CostCode;
import com.sinapipro.api.jobcosting.domain.CostCodeRepository;
import com.sinapipro.api.jobcosting.domain.CostTransaction;
import com.sinapipro.api.jobcosting.domain.CostTransactionRepository;
import com.sinapipro.api.jobcosting.domain.CostTransactionType;
import com.sinapipro.api.measurement.domain.*;
import com.sinapipro.api.shared.error.DomainNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MeasurementService {

    private final MeasurementRepository measurementRepository;
    private final BudgetRepository budgetRepository;
    private final BudgetItemRepository budgetItemRepository;
    private final CostCodeRepository costCodeRepository;
    private final CostTransactionRepository costTransactionRepository;
    private final InvoiceRepository invoiceRepository;
    private final ReceivableRepository receivableRepository;

    public MeasurementService(MeasurementRepository measurementRepository, BudgetRepository budgetRepository,
                              BudgetItemRepository budgetItemRepository,
                              CostCodeRepository costCodeRepository, CostTransactionRepository costTransactionRepository,
                              InvoiceRepository invoiceRepository, ReceivableRepository receivableRepository) {
        this.measurementRepository = measurementRepository;
        this.budgetRepository = budgetRepository;
        this.budgetItemRepository = budgetItemRepository;
        this.costCodeRepository = costCodeRepository;
        this.costTransactionRepository = costTransactionRepository;
        this.invoiceRepository = invoiceRepository;
        this.receivableRepository = receivableRepository;
    }

    @Transactional
    public Measurement create(UUID budgetId, Integer number, LocalDate periodStart, LocalDate periodEnd,
                              BigDecimal retentionPct, List<ItemInput> items) {
        var budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new DomainNotFoundException("Budget not found: " + budgetId));
        var m = new Measurement(budget, number, periodStart, periodEnd, retentionPct);
        for (var item : items) {
            if (item.budgetItemId() != null) {
                BudgetItem budgetItem = budgetItemRepository.findById(item.budgetItemId())
                        .orElseThrow(() -> new DomainNotFoundException("Budget item not found: " + item.budgetItemId()));
                validateMeasuredQuantity(budgetId, number, budgetItem, item.quantity());
                m.getItems().add(new MeasurementItem(m, budgetItem,
                        item.description() != null ? item.description() : budgetItem.getComposition().getDescription(),
                        item.quantity(), item.unitPrice() != null ? item.unitPrice() : budgetItem.getUnitCost()));
            } else {
                m.getItems().add(new MeasurementItem(m, item.costCodeId(), item.description(), item.quantity(), item.unitPrice()));
            }
        }
        return measurementRepository.save(m);
    }

    public List<AvailableBudgetItem> availableBudgetItems(UUID budgetId) {
        return budgetItemRepository.findAllByBudgetId(budgetId).stream()
                .map(item -> {
                    BigDecimal previous = measuredQuantity(budgetId, Integer.MAX_VALUE, item.getId());
                    BigDecimal balance = item.getQuantity().subtract(previous);
                    return new AvailableBudgetItem(
                            item.getId(),
                            item.getComposition().getSinapiCode(),
                            item.getComposition().getDescription(),
                            item.getComposition().getUnit(),
                            item.getQuantity(),
                            previous,
                            balance.max(BigDecimal.ZERO),
                            item.getUnitCost());
                })
                .toList();
    }

    @Transactional
    public Measurement submit(UUID measurementId) {
        var m = findOrThrow(measurementId);
        m.submit();
        return measurementRepository.save(m);
    }

    @Transactional
    public Measurement approve(UUID measurementId) {
        var m = findOrThrow(measurementId);
        m.approve();
        var saved = measurementRepository.save(m);

        // Virtual threads: cost transactions and invoice generation run in parallel
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var costFuture = executor.submit(() -> {
                generateCostTransactions(saved);
                return null;
            });
            var invoiceFuture = executor.submit(() -> {
                generateInvoiceFromMeasurement(saved);
                return null;
            });
            costFuture.get();
            invoiceFuture.get();
        } catch (ExecutionException e) {
            throw new RuntimeException("Approval failed for measurement: " + measurementId, e.getCause());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Approval interrupted for measurement: " + measurementId, e);
        }

        return saved;
    }

    private void generateCostTransactions(Measurement saved) {
        for (var item : saved.getItems()) {
            if (item.getCostCodeId() != null) {
                costCodeRepository.findById(item.getCostCodeId()).ifPresent(costCode -> {
                    if (!costTransactionRepository.existsByReferenceIdAndType(saved.getId(), CostTransactionType.ACTUAL)) {
                        var tx = new CostTransaction(costCode, CostTransactionType.ACTUAL,
                                item.getAmount().setScale(2, RoundingMode.HALF_UP),
                                "Measurement #" + saved.getNumber() + ": " + item.getDescription(),
                                saved.getId(), saved.getPeriodEnd());
                        costTransactionRepository.save(tx);
                    }
                });
            }
        }
    }

    private void generateInvoiceFromMeasurement(Measurement measurement) {
        var invoiceNumber = "MED-" + measurement.getBudget().getId().toString().substring(0, 8)
                + "-" + measurement.getNumber();
        if (invoiceRepository.existsByNumber(invoiceNumber)) return;

        var invoice = new Invoice(invoiceNumber, measurement.getBudget(), null,
                measurement.getNetAmount().setScale(2, RoundingMode.HALF_UP),
                measurement.getPeriodEnd(), measurement.getPeriodEnd().plusDays(30),
                InvoiceStatus.PENDING,
                "Auto-generated from Measurement #" + measurement.getNumber());
        invoiceRepository.save(invoice);

        // Generate receivable (conta a receber)
        var receivable = new Receivable(
                measurement.getBudget().getId(),
                "Medição #" + measurement.getNumber() + " — " + measurement.getBudget().getTitle(),
                measurement.getNetAmount().setScale(2, RoundingMode.HALF_UP),
                measurement.getPeriodEnd().plusDays(30),
                "MEASUREMENT");
        receivable.setMeasurementId(measurement.getId());
        receivable.setInvoiceId(invoice.getId());
        receivableRepository.save(receivable);
    }

    public MeasurementSummary summary(UUID budgetId) {
        var all = measurementRepository.findByBudgetIdOrderByNumberDesc(budgetId);

        record Totals(BigDecimal gross, BigDecimal net, BigDecimal retention) {}

        var totals = all.stream()
                .filter(m -> m.getStatus() == MeasurementStatus.APPROVED || m.getStatus() == MeasurementStatus.PAID)
                .gather(Gatherers.fold(
                        () -> new Totals(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO),
                        (acc, m) -> {
                            var gross = m.getGrossAmount();
                            var net = m.getNetAmount();
                            return new Totals(acc.gross().add(gross), acc.net().add(net), acc.retention().add(gross.subtract(net)));
                        }
                ))
                .findFirst()
                .orElse(new Totals(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));

        return new MeasurementSummary(all.size(), totals.gross(), totals.net(), totals.retention());
    }

    public CumulativeResult cumulative(UUID budgetId, UUID measurementId) {
        var all = measurementRepository.findByBudgetIdOrderByNumberDesc(budgetId);
        var current = findOrThrow(measurementId);

        var previousCumulative = all.stream()
                .filter(m -> m.getNumber() < current.getNumber()
                        && (m.getStatus() == MeasurementStatus.APPROVED || m.getStatus() == MeasurementStatus.PAID))
                .map(Measurement::getGrossAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        var currentGross = current.getGrossAmount();
        var totalCumulative = previousCumulative.add(currentGross);

        return new CumulativeResult(previousCumulative, currentGross, totalCumulative);
    }

    public BalanceResult balance(UUID budgetId, BigDecimal contractedTotal) {
        var all = measurementRepository.findByBudgetIdOrderByNumberDesc(budgetId);

        var measured = all.stream()
                .filter(m -> m.getStatus() == MeasurementStatus.APPROVED || m.getStatus() == MeasurementStatus.PAID)
                .map(Measurement::getGrossAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        var remaining = contractedTotal.subtract(measured);
        var percentMeasured = contractedTotal.compareTo(BigDecimal.ZERO) > 0
                ? measured.multiply(BigDecimal.valueOf(100)).divide(contractedTotal, 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return new BalanceResult(contractedTotal, measured, remaining, percentMeasured);
    }

    public MeasurementDetail detail(UUID measurementId) {
        Measurement measurement = findOrThrow(measurementId);
        var lines = measurement.getItems().stream()
                .map(item -> {
                    UUID budgetItemId = item.getBudgetItemId();
                    BigDecimal contracted = item.getBudgetItem() != null ? item.getBudgetItem().getQuantity() : item.getQuantity();
                    BigDecimal previous = budgetItemId != null
                            ? measuredQuantity(measurement.getBudget().getId(), measurement.getNumber(), budgetItemId)
                            : BigDecimal.ZERO;
                    BigDecimal cumulative = previous.add(item.getQuantity());
                    BigDecimal balance = contracted.subtract(cumulative);
                    return new MeasurementLineDetail(
                            item.getId(), budgetItemId, item.getDescription(), item.getQuantity(), item.getUnitPrice(),
                            item.getAmount(), contracted, previous, cumulative, balance);
                })
                .toList();
        return new MeasurementDetail(measurement.getId(), measurement.getNumber(), measurement.getPeriodStart(),
                measurement.getPeriodEnd(), measurement.getStatus().name(), measurement.getRetentionPct(),
                measurement.getGrossAmount(), measurement.getNetAmount(), lines);
    }

    private void validateMeasuredQuantity(UUID budgetId, Integer measurementNumber, BudgetItem budgetItem, BigDecimal periodQuantity) {
        BigDecimal previous = measuredQuantity(budgetId, measurementNumber, budgetItem.getId());
        BigDecimal cumulative = previous.add(periodQuantity);
        if (cumulative.compareTo(budgetItem.getQuantity()) > 0) {
            throw new DomainValidationException("Measured quantity exceeds contracted quantity for item "
                    + budgetItem.getComposition().getSinapiCode());
        }
    }

    private BigDecimal measuredQuantity(UUID budgetId, Integer beforeMeasurementNumber, UUID budgetItemId) {
        return measurementRepository.findByBudgetIdOrderByNumberDesc(budgetId).stream()
                .filter(m -> m.getNumber() < beforeMeasurementNumber)
                .filter(m -> m.getStatus() == MeasurementStatus.APPROVED || m.getStatus() == MeasurementStatus.PAID)
                .flatMap(m -> m.getItems().stream())
                .filter(item -> budgetItemId.equals(item.getBudgetItemId()))
                .map(MeasurementItem::getQuantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Measurement findOrThrow(UUID id) {
        return measurementRepository.findById(id)
                .orElseThrow(() -> new DomainNotFoundException("Measurement not found: " + id));
    }

    public record ItemInput(UUID costCodeId, UUID budgetItemId, String description, BigDecimal quantity, BigDecimal unitPrice) {}
    public record MeasurementSummary(int totalMeasurements, BigDecimal totalGross, BigDecimal totalNet, BigDecimal totalRetention) {}
    public record CumulativeResult(BigDecimal previousCumulative, BigDecimal currentPeriod, BigDecimal totalCumulative) {}
    public record BalanceResult(BigDecimal contractedTotal, BigDecimal measured, BigDecimal remaining, BigDecimal percentMeasured) {}
    public record AvailableBudgetItem(UUID budgetItemId, String code, String description, String unit,
                                      BigDecimal contractedQuantity, BigDecimal previousQuantity,
                                      BigDecimal balanceQuantity, BigDecimal unitPrice) {}
    public record MeasurementDetail(UUID id, Integer number, LocalDate periodStart, LocalDate periodEnd, String status,
                                    BigDecimal retentionPct, BigDecimal grossAmount, BigDecimal netAmount,
                                    List<MeasurementLineDetail> items) {}
    public record MeasurementLineDetail(UUID id, UUID budgetItemId, String description, BigDecimal periodQuantity,
                                        BigDecimal unitPrice, BigDecimal periodAmount, BigDecimal contractedQuantity,
                                        BigDecimal previousQuantity, BigDecimal cumulativeQuantity, BigDecimal balanceQuantity) {}
}
