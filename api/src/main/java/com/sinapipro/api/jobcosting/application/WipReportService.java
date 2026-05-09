package com.sinapipro.api.jobcosting.application;

import module java.base;

import com.sinapipro.api.jobcosting.domain.CostTransactionRepository;
import com.sinapipro.api.jobcosting.domain.CostTransactionType;
import com.sinapipro.api.measurement.domain.Measurement;
import com.sinapipro.api.measurement.domain.MeasurementRepository;
import com.sinapipro.api.measurement.domain.MeasurementStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * WIP Report (Work in Progress) — REQ-1.6
 * Compares billed amount (measurements) vs actual cost to determine over/under billing.
 */
@Service
@Transactional(readOnly = true)
public class WipReportService {

    private final CostTransactionRepository costTransactionRepository;
    private final MeasurementRepository measurementRepository;

    public WipReportService(CostTransactionRepository costTransactionRepository,
                            MeasurementRepository measurementRepository) {
        this.costTransactionRepository = costTransactionRepository;
        this.measurementRepository = measurementRepository;
    }

    public WipReport calculate(UUID budgetId) {
        var actualCost = costTransactionRepository.sumByBudgetAndType(budgetId, CostTransactionType.ACTUAL);

        // Use Gatherers.fold to aggregate billed amount from approved/paid measurements
        var measurements = measurementRepository.findByBudgetIdOrderByNumberDesc(budgetId);
        var billedAmount = measurements.stream()
                .filter(m -> m.getStatus() == MeasurementStatus.APPROVED || m.getStatus() == MeasurementStatus.PAID)
                .map(Measurement::getGrossAmount)
                .gather(Gatherers.fold(() -> BigDecimal.ZERO, BigDecimal::add))
                .findFirst()
                .orElse(BigDecimal.ZERO);

        var wipVariance = billedAmount.subtract(actualCost);
        var status = switch (wipVariance.signum()) {
            case 1 -> WipStatus.OVER_BILLED;
            case -1 -> WipStatus.UNDER_BILLED;
            default -> WipStatus.BALANCED;
        };

        var billingRatio = actualCost.compareTo(BigDecimal.ZERO) > 0
                ? billedAmount.divide(actualCost, 4, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return new WipReport(actualCost, billedAmount, wipVariance, status, billingRatio);
    }

    public enum WipStatus { OVER_BILLED, UNDER_BILLED, BALANCED }

    public record WipReport(BigDecimal actualCost, BigDecimal billedAmount, BigDecimal wipVariance,
                            WipStatus status, BigDecimal billingRatio) {}
}
