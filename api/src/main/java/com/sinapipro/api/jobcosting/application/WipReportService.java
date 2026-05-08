package com.sinapipro.api.jobcosting.application;

import com.sinapipro.api.jobcosting.domain.CostTransactionRepository;
import com.sinapipro.api.jobcosting.domain.CostTransactionType;
import com.sinapipro.api.measurement.domain.Measurement;
import com.sinapipro.api.measurement.domain.MeasurementRepository;
import com.sinapipro.api.measurement.domain.MeasurementStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

/**
 * WIP Report (Work in Progress) — REQ-1.6
 * Compares billed amount (measurements) vs actual cost to determine over/under billing.
 *
 * Over-billing: billed > actual cost (liability — work not yet performed)
 * Under-billing: actual cost > billed (asset — work performed but not yet billed)
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
        // Actual costs incurred
        BigDecimal actualCost = costTransactionRepository.sumByBudgetAndType(budgetId, CostTransactionType.ACTUAL);

        // Billed amount (approved/paid measurements gross)
        List<Measurement> measurements = measurementRepository.findByBudgetIdOrderByNumberDesc(budgetId);
        BigDecimal billedAmount = BigDecimal.ZERO;
        for (Measurement m : measurements) {
            if (m.getStatus() == MeasurementStatus.APPROVED || m.getStatus() == MeasurementStatus.PAID) {
                billedAmount = billedAmount.add(m.getGrossAmount());
            }
        }

        // WIP calculation
        BigDecimal wipVariance = billedAmount.subtract(actualCost);
        WipStatus status;
        if (wipVariance.compareTo(BigDecimal.ZERO) > 0) {
            status = WipStatus.OVER_BILLED;
        } else if (wipVariance.compareTo(BigDecimal.ZERO) < 0) {
            status = WipStatus.UNDER_BILLED;
        } else {
            status = WipStatus.BALANCED;
        }

        // Billing ratio
        BigDecimal billingRatio = actualCost.compareTo(BigDecimal.ZERO) > 0
                ? billedAmount.divide(actualCost, 4, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return new WipReport(actualCost, billedAmount, wipVariance, status, billingRatio);
    }

    public enum WipStatus { OVER_BILLED, UNDER_BILLED, BALANCED }

    public record WipReport(BigDecimal actualCost, BigDecimal billedAmount, BigDecimal wipVariance,
                            WipStatus status, BigDecimal billingRatio) {}
}
